package com.desmond.ptrcomarison.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.desmond.ptrcomarison.R;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

/**
 * Created by desmond on 2015/12/9.
 */
public class CommonListAdapter extends BaseAdapter {
    public static final String URL_HEAD = "http://desmondtu.oss-cn-shanghai.aliyuncs.com/ptrcomp/";
    public static final int ITEM_COUNT = 15;
    public static final int TOTAL_COUNT = 30;
    private Context mContext;
    private int mStartOffset = 1;
    private Object mRequestTag;
    private static Picasso sPicasso = null;

    public CommonListAdapter(Context context) {
        mContext = context;
        tryInitPicasso(context);
    }

    private static void tryInitPicasso(Context context){
        if(sPicasso != null) return;
        sPicasso = new Picasso.Builder(context)
                .memoryCache(new MyCache(5))
                .defaultBitmapConfig(Bitmap.Config.ARGB_4444)
                .build();
    }

    public void setRequestTag(Object requestTag) {
        this.mRequestTag = requestTag;
    }

    @Override
    public int getCount() {
        return ITEM_COUNT;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;
        if(convertView == null){
            view = View.inflate(mContext, R.layout.list_item, null);
            holder = new ViewHolder();
            holder.image = (ImageView) view.findViewById(R.id.image);
            holder.title = (TextView) view.findViewById(R.id.title);
            holder.content = (TextView) view.findViewById(R.id.content);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) convertView.getTag();
        }

        sPicasso.load(getImageUrl(position + mStartOffset))
                .tag(mRequestTag)
                .into(holder.image);

        holder.title.setText(mContext.getString(R.string.list_item_title, position));
        holder.content.setText(mContext.getString(R.string.list_item_content, position));

        return view;
    }

    public void nextPage(){
        mStartOffset += ITEM_COUNT;
        mStartOffset %= TOTAL_COUNT;
        notifyDataSetChanged();
    }

    private String getImageUrl(int position){
        return URL_HEAD + position + ".jpg";
    }

    class ViewHolder{
        ImageView image;
        TextView title;
        TextView content;
    }

    static class MyCache extends LruCache{

        public MyCache(int maxSize) {
            super(maxSize);
        }
    }
}
