package com.desmond.ptrcomarison.common;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.desmond.ptrcomarison.R;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Created by desmond on 2015/12/9.
 */
public class CommonListAdapter extends BaseAdapter {
    public static final String URL_HEAD = "http://desmondtu.oss-cn-shanghai.aliyuncs.com/ptr_comp/";
    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_HORIZONTAL_SCROLLABLE = 1;
    public static final int ITEM_COUNT = 5;
    public static final int TOTAL_COUNT = 20;
    private Context mContext;
    private int mStartOffset = 1;

    public CommonListAdapter(Context context) {
        mContext = context;
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
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            if (getItemViewType(position) == TYPE_NORMAL) {
                convertView = View.inflate(mContext, R.layout.list_item, null);
                holder.draweeView = (SimpleDraweeView) convertView.findViewById(R.id.image);
                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.content = (TextView) convertView.findViewById(R.id.content);
            } else {
                convertView = View.inflate(mContext, R.layout.list_item_with_horcroll, null);
                holder.recyclerView = (RecyclerView) convertView.findViewById(R.id.recycler_view);
            }
            convertView.setTag(holder);
        }

        holder = (ViewHolder) convertView.getTag();
        setViewData(position, holder);
        return convertView;
    }

    private void setViewData(int position, ViewHolder holder) {
        if (getItemViewType(position) == TYPE_NORMAL) {
            holder.draweeView.setImageURI(Uri.parse(getImageUrl(position + mStartOffset)));
            holder.title.setText(mContext.getString(R.string.list_item_title, position));
            holder.content.setText(mContext.getString(R.string.list_item_content, position));
        } else {
            if (holder.recyclerView.getLayoutManager() == null) {
                LinearLayoutManager lm = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);

                holder.recyclerView.setLayoutManager(lm);
            }

            if (holder.recyclerView.getAdapter() == null) {
                holder.recyclerView.setAdapter(new RecyclerAdapter());
            }
        }
    }

    public void nextPage() {
        mStartOffset += ITEM_COUNT;
        mStartOffset %= TOTAL_COUNT;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position != 2) {
            return TYPE_NORMAL;
        }
        return TYPE_HORIZONTAL_SCROLLABLE;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    public int dp2px(int dp) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dp * scale);
    }

    private String getImageUrl(int position) {
        return URL_HEAD + position + ".jpg";
    }

    class ViewHolder {
        SimpleDraweeView draweeView;
        TextView title;
        TextView content;
        RecyclerView recyclerView;
    }

    class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerHolder> {
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
