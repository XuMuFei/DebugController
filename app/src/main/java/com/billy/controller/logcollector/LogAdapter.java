package com.billy.controller.logcollector;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * @author billy.qi
 * @since 17/5/25 20:42
 */
public class LogAdapter extends Adapter {

    List<String> data;

    public LogAdapter(List<String> data) {
        this.data = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.log_item, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        String content = null;
        if (position >= 0 && position < data.size()) {
            content = data.get(position);
        }
        ((Holder)holder).bind(content);
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    private class Holder extends RecyclerView.ViewHolder {
        private final TextView textView;

        Holder(View itemView) {
            super(itemView);
            this.textView = (TextView) itemView.findViewById(R.id.log_content);
            this.textView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    String text = textView.getText().toString().trim();
                    Context context = v.getContext().getApplicationContext();
                    //获取剪贴板管理器：
                    ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    // 创建普通字符型ClipData
                    ClipData mClipData = ClipData.newPlainText("Label", text);
                    // 将ClipData内容放到系统剪贴板里。
                    cm.setPrimaryClip(mClipData);

                    Intent intent=new Intent(Intent.ACTION_SEND);
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Share");
                    intent.putExtra(Intent.EXTRA_TEXT, text);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    textView.getContext().startActivity(Intent.createChooser(intent, context.getString(R.string.share)));
                    Toast.makeText(context, R.string.copy_to_clipborad_success, Toast.LENGTH_SHORT).show();
                    return false;
                }
            });
        }
        void bind(String content) {
            if (content == null) {
                content = "";
            }
            textView.setText(content);
        }
    }
}
