package utm.crawler.lab4;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements Crawler.CrawlerCompleteListener, Crawler.ProgressDisplay {
    private ProgressDialog progressDialog;
    private Handler handler;
    @BindView(R.id.matches)
    TextView matches;
    @BindView(R.id.listView)
    ListView listView;
    @BindView(R.id.level)
    EditText level;
    @BindView(R.id.link)
    EditText link;
    @BindView(R.id.keyword)
    EditText keyword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        handler = new Handler();
        matches.setVisibility(View.GONE);
    }

    private void start() {
        String parseUrl = link.getText().toString();
        try {
            URI uri = new URI(parseUrl);
            if (uri.getPath() == null && uri.getHost() == null)
                throw new Exception("Wrong link!");
        } catch (Exception e){
            Toast.makeText(this,"Invalid link!",Toast.LENGTH_SHORT).show();
            return;
        }
        int level = Integer.valueOf(this.level.getText().toString());
        if (level <=0) {
            Toast.makeText(this, "Invalid level!", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog = ProgressDialog.show(this, "Please wait", "Processing links");
        Crawler crawler = new Crawler(getApplicationContext(), this, this);
        crawler.processLinks(parseUrl, level, keyword.getText().toString());
        matches.setVisibility(View.GONE);
    }

    @Override
    public void onComplete(List<Crawler.FoundLink> list, int keywordMatches) {
        progressDialog.dismiss();
        LinkAdapter linkAdapter = new LinkAdapter(this, list);
        listView.setAdapter(linkAdapter);
        matches.setVisibility(View.VISIBLE);
        matches.setText("Keyword matches: "+keywordMatches);
    }

    @Override
    public void setProgress(final String title, final String subtitle) {
        if (progressDialog == null) return;
        handler.post(new Runnable() {
            @Override
            public void run() {
                progressDialog.setTitle(title);
                progressDialog.setMessage(subtitle);
            }
        });
    }

    @OnClick(R.id.start)
    public void onStartClick(){
        start();
    }
}
