package utm.crawler.lab4;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by alexandr on 02.04.17.
 */

public class Crawler {
    private Context context;
    private CrawlerCompleteListener crawlerCompleteListener;
    private List<FoundLink> allLinks;
    private int levelLimit;
    private String keyword;
    private int keywordCount;
    private Handler handler;
    private ProgressDisplay progressDisplay;
    private int visitedCount;

    public Crawler(Context context, CrawlerCompleteListener crawlerCompleteListener, ProgressDisplay progressDisplay) {
        this.context = context;
        this.crawlerCompleteListener = crawlerCompleteListener;
        this.handler = new Handler();
        this.allLinks = new ArrayList<>();
        this.progressDisplay = progressDisplay;
    }

    public void processLinks(final String url, int lvl, String keyword) {
        final String host;
        this.keyword = keyword;
        this.levelLimit = lvl;
        this.keywordCount = 0;
        this.visitedCount = 0;
        final int level = lvl - 1;
        if (lvl < 0)
            return;
        try {
            final URI baseUrl = new URI(url);
            host = baseUrl.getHost();
        } catch (Exception e) {
            return;
        }
        (new AsyncTask<Void, Void, Void>() {
            @Override
            public void onPreExecute() {

            }

            @Override
            public Void doInBackground(Void... params) {

                getLinks(host, url, level);
                //processPages();
                return null;
            }

            @Override
            public void onPostExecute(Void result) {
                Log.d("RESULT", "TOTAL LINKS = " + allLinks.size());
                Log.d("RESULT", "KEYWORD ENTRIES = " + keywordCount);
                crawlerCompleteListener.onComplete(new ArrayList<>(allLinks), keywordCount);
            }
        }).execute();

    }

    private void processPages() {
        for (FoundLink link : allLinks)
            if (link.getKeywordCount() == -1) {
                int count = 0;
                try {
                    Document doc = Jsoup.connect(link.getLink()).get();
                    count = getKeywordCount(doc.body().text(), keyword);
                    keywordCount += count;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                link.setKeywordCount(count);
                Log.d("RESULT", "VISITING " + link.getLink() + " KEYWORD ENTRIES = " + count);
                progressDisplay.setProgress("VISITING ("+visitedCount+"/"+allLinks.size()+")",link.getLink());
            }
    }

    private void getLinks(String host, String adr, int level) {
        try {
            Document doc = Jsoup.connect(adr).get();
            Elements links = doc.select("a[href]");
            int count = getKeywordCount(doc.body().text(), keyword);
            keywordCount += count;
            allLinks.add(new FoundLink(adr, (levelLimit - level)));
            Log.d("RESULT", "VISITING " + adr + " KEYWORD ENTRIES = " + count);
            for (Element link : links) {
                String url = link.attr("href");
                try {
                    URI uri = new URI(url);
                    if (uri.getHost() == null && uri.getPath() == null)
                        continue;
                    if (uri.getHost() == null) {
                        String path = uri.getPath();
                        if (path.length()>0)
                        if (path.charAt(0) != '/')
                            path = "/" + path;
                        url = "http://" + host + path;
                        Log.d("RESULT","PATH = "+uri.getPath());
                    }else if (url.charAt(0) == '/')
                        url = "http:" + url;
                    FoundLink foundLink = new FoundLink(url,levelLimit - level);
                    int index = allLinks.indexOf(foundLink);
                    if (index < 0) {
                        allLinks.add(foundLink);
                        progressDisplay.setProgress("COLLECTING LINKS", "TOTAL: "+allLinks.size());
                        Log.d("RESULT", "HOST = "+host+"LEVEL = "+level+" LIMIT = "+levelLimit+" LEVEL = " + (levelLimit - level) + " LINK = " + url);
                        if (level > 0)
                            getLinks(host, url, level - 1);
                    } else
                        if (level > 0 && allLinks.get(index).getLevel()>foundLink.getLevel() && !allLinks.get(index).isVisited()) {
                            foundLink.setVisited(true);
                            allLinks.remove(index);
                            allLinks.add(foundLink);
                            getLinks(host, url, level - 1);
                        }
                } catch (URISyntaxException u) {
                    continue;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getKeywordCount(String text, String keyword) {
        visitedCount++;
        long cur = System.currentTimeMillis();
        text = text.toLowerCase();
        keyword = keyword.toLowerCase();
        int lastIndex = 0;
        int count = 0;

        while (lastIndex != -1) {

            lastIndex = text.indexOf(keyword, lastIndex);

            if (lastIndex != -1) {
                count++;
                lastIndex += keyword.length();
            }
        }
        Log.d("RESULT", "COUNTING TOOK " + (System.currentTimeMillis() - cur));
        return count;
    }

    private static String performGetCall(String requestURL) {
        Log.d("RESULT","GETTING "+requestURL);
        URL url;
        String response = "";
        try {
            url = new URL(requestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(7000);
            conn.setConnectTimeout(7000);
            conn.setRequestMethod("GET");
            conn.setInstanceFollowRedirects(true);
            conn.setRequestProperty("User-agent", System.getProperty("http.agent"));
            conn.setDoInput(true);
            int responseCode = conn.getResponseCode();
            if (responseCode < 400) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
            } else {
                response = "";

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    interface CrawlerCompleteListener {
        void onComplete(List<FoundLink> list, int keywordMatches);
    }

    interface ProgressDisplay {
        void setProgress(String title, String subtitle);
    }

    public class FoundLink implements Comparable {
        private String link;
        private int level;
        private int keywordCount;
        private boolean isVisited;

        public FoundLink(String link, int level) {
            this.link = link;
            this.level = level;
            this.keywordCount = -1;
            this.isVisited = false;
        }

        public boolean isVisited() {
            return isVisited;
        }

        public void setVisited(boolean visited) {
            isVisited = visited;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public int getKeywordCount() {
            return keywordCount;
        }

        public void setKeywordCount(int keywordCount) {
            this.keywordCount = keywordCount;
        }

        @Override
        public int hashCode() {
            return link.hashCode();
        }

        @Override
        public int compareTo(Object obj) {
            if (!(obj instanceof FoundLink))
                return -1;
            FoundLink foundLink = (FoundLink) obj;
            return link.compareTo(foundLink.getLink());
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof FoundLink))
                return false;
            return obj == this || ((FoundLink)obj).getLink().equals(link);
        }
    }

}
