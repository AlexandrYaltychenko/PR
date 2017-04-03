package utm.crawler.lab4;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by alexandr on 02.04.17.
 */

public class LinkAdapter extends BaseAdapter {
    private List<Crawler.FoundLink> links;
    private Context context;
    private LayoutInflater layoutInflater;

    public LinkAdapter(Context context, List<Crawler.FoundLink> links) {
        this.context = context;
        this.links = links;
        this.layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return links.size();
    }

    @Override
    public Crawler.FoundLink getItem(int position) {
        return links.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = layoutInflater.inflate(R.layout.link_item,parent,false);
        LinkHolder holder = (LinkHolder) convertView.getTag();
        if (holder == null) {
            holder = new LinkHolder(convertView);
            convertView.setTag(holder);
        }
        Crawler.FoundLink link = getItem(position);
        holder.count.setText(String.valueOf(link.getKeywordCount()));
        holder.link.setText(link.getLink());
        holder.level.setText("Level: "+String.valueOf(link.getLevel()));
        return convertView;
    }

    class LinkHolder {
        @BindView(R.id.link)
        TextView link;
        @BindView(R.id.count)
        TextView count;
        @BindView(R.id.level)
        TextView level;

        public LinkHolder(View view){
            ButterKnife.bind(this, view);
        }
    }
}
