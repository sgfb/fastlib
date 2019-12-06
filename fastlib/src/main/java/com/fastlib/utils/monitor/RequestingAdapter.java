package com.fastlib.utils.monitor;

import android.text.format.Formatter;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fastlib.R;
import com.fastlib.adapter.BaseRecyAdapter;
import com.fastlib.annotation.ContentView;
import com.fastlib.base.CommonViewHolder;

import java.io.File;
import java.util.Locale;

public class RequestingAdapter extends BaseRecyAdapter<Requesting>{

    public RequestingAdapter() {
        super(R.layout.item_requesting);
    }

    @Override
    public void binding(int position, Requesting data, CommonViewHolder holder) {
        holder.setVisibility(R.id.progress, data.type == Requesting.TYPE_DOWNLOADING || data.type == Requesting.TYPE_UPLOADING ? View.VISIBLE : View.GONE);
        holder.setText(R.id.urlSegment, data.url);
        holder.setText(R.id.secondStatus,"");

        TextView status=holder.getView(R.id.status);
        switch (data.status) {
            case Requesting.STATUS_WAITING:
                status.setTextColor(mContext.getResources().getColor(R.color.grey_600));
                status.setText("waiting");
                break;
            case Requesting.STATUS_REQUESTING:
                status.setTextColor(mContext.getResources().getColor(R.color.grey_600));

                ProgressBar progressBar=holder.getView(R.id.progress);
                if(data.type == Requesting.TYPE_DOWNLOADING&&data.downloading!=null){
                    File file=new File(data.downloading.getPath());
                    int percent= (int) (file.length()*100/data.downloading.getMaxLength());
                    progressBar.setProgress(percent);
                    status.setText(String.format(Locale.getDefault(),"%d%%",percent));
                    holder.setText(R.id.secondStatus,Formatter.formatFileSize(mContext,data.downloading.getSpeed()));
                }
                else if(data.type==Requesting.TYPE_UPLOADING&&data.uploading!=null){
                    File file=new File(data.uploading.getPath());
                    int percent= (int) (data.uploading.getSendByte()*100/file.length());
                    progressBar.setProgress(percent);
                    status.setText(String.format(Locale.getDefault(),"%d%%",percent));
                    holder.setText(R.id.secondStatus,Formatter.formatFileSize(mContext,data.uploading.getSpeed()));
                }
                else {
                    progressBar.setProgress(0);
                    status.setText("requesting");
                }
                break;
            case Requesting.STATUS_SUCCESS:
                holder.setVisibility(R.id.progress,View.GONE);
                status.setText("success");
                status.setTextColor(mContext.getResources().getColor(R.color.green_600));
                holder.setText(R.id.secondStatus,String.format(Locale.getDefault(),"%s | %dms",Formatter.formatFileSize(mContext,data.contentLength),data.timeConsume));
                break;
            case Requesting.STATUS_ERROR:
                status.setText("error");
                status.setTextColor(mContext.getResources().getColor(R.color.red_600));
                break;
            case Requesting.STATUS_EXCEPTION:
                status.setText("exception");
                status.setTextColor(mContext.getResources().getColor(R.color.yellow_600));
                break;
            default:
                System.out.println("requesting status undefined");
                break;
        }
    }

    public synchronized void removeRequest(int hashCode){
        Requesting requesting=getRequestByHash(hashCode);
        remove(requesting);
        notifyDataSetChanged();
    }

    public synchronized Requesting getRequestByHash(int hashCode){
        for(Requesting requesting:getData()){
            if(requesting.hashCode()==hashCode)
                return requesting;
        }
        return null;
    }
}
