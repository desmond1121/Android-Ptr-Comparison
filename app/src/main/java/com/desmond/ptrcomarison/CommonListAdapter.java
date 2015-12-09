package com.desmond.ptrcomarison;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by Jiayi Yao on 2015/12/9.
 */
public class CommonListAdapter extends BaseAdapter {
    public static final String URL_HEAD = "http://desmondtu.oss-cn-shanghai.aliyuncs.com/ptrcomp/";
    public static final int ITEM_COUNT = 15;
    public static final int TOTAL_COUNT = 30;
    private Context mContext;
    private int mStartOffset = 1;
    private Object mRequestTag;

    public CommonListAdapter(Context context) {
        mContext = context;
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

        Picasso.with(mContext)
                .load(getImageUrl(position + mStartOffset))
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
}
