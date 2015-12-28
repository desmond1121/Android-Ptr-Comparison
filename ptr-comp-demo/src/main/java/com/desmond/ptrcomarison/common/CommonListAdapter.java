package com.desmond.ptrcomarison.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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
    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_HORIZONTAL_SCROLLABLE = 1;
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
            if(getItemViewType(position) == TYPE_NORMAL){
                convertView = View.inflate(mContext, R.layout.list_item, null);
                holder = new ViewHolder();
                holder.image = (ImageView) convertView.findViewById(R.id.image);
                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.content = (TextView) convertView.findViewById(R.id.content);
                convertView.setTag(holder);
            } else {
                convertView = View.inflate(mContext, R.layout.list_item_with_horcroll, null);
                holder = new ViewHolder();
                holder.recyclerView = (RecyclerView) convertView.findViewById(R.id.recycler_view);
                convertView.setTag(holder);
            }
        }

        view = convertView;
        holder = (ViewHolder) convertView.getTag();
        setViewData(position, holder);

        return view;
    }

    private void setViewData(int position, ViewHolder holder) {
        if(getItemViewType(position) == TYPE_NORMAL) {
            sPicasso.load(getImageUrl(position + mStartOffset))
                    .tag(mRequestTag)
                    .into(holder.image);
            holder.title.setText(mContext.getString(R.string.list_item_title, position));
            holder.content.setText(mContext.getString(R.string.list_item_content, position));
        } else {
            if(holder.recyclerView.getLayoutManager() == null){
                LinearLayoutManager lm = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);

                holder.recyclerView.setLayoutManager(lm);
            }

            if(holder.recyclerView.getAdapter() == null){
                holder.recyclerView.setAdapter(new RecyclerAdapter());
            }
        }
    }

    public void nextPage(){
        mStartOffset += ITEM_COUNT;
        mStartOffset %= TOTAL_COUNT;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if(position != 2){
            return TYPE_NORMAL;
        }
        return TYPE_HORIZONTAL_SCROLLABLE;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    private String getImageUrl(int position){
        return URL_HEAD + position + ".jpg";
    }

    static class MyCache extends LruCache{
        public MyCache(int maxSize) {
            super(maxSize);
        }
    }

    class ViewHolder{
        ImageView image;
        TextView title;
        TextView content;
        RecyclerView recyclerView;
    }

    class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerHolder>{
        @Override
        public RecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecyclerHolder(LayoutInflater.from(mContext).inflate(R.layout.sub_list_item, null));
        }

        @Override
        public void onBindViewHolder(RecyclerHolder holder, int position) {
            holder.tv.setText("Horizontal Scrollable");
        }

        @Override
        public int getItemCount() {
            return 10;
        }

        class RecyclerHolder extends RecyclerView.ViewHolder {
            TextView tv;
            public RecyclerHolder(View itemView) {
                super(itemView);
                tv = (TextView) itemView.findViewById(R.id.text_view);
            }
        }
    }
}
