package com.proxyfortelegram.powerful.proxy.fastproxy.proxy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.proxyfortelegram.powerful.proxy.fastproxy.R;
import com.proxyfortelegram.powerful.proxy.fastproxy.helpers.IClickListner;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Random;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class ProxyAdapter extends RecyclerView.Adapter<ProxyAdapter.ViewHolder> {

    private Context context;
    private List<ListModel> listModel;
    private SharedPreferences sharedPreferences;
    private IClickListner clickListner;

    //todo
    public ProxyAdapter(Context context, List<ListModel> list, IClickListner clickListner) {
        this.context = context;
        this.listModel = list;
        this.clickListner = clickListner;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public int getItemCount() {
        return listModel.size();
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        speed(holder);
        holder.bind(listModel.get(position));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public Button btn_telegram;
        public ImageButton btn_share;
        public ImageView img_country;
        public TextView textpublish, textlocation, textrandom;
        private ImageView statusColor;
        private ListModel data;

        public void bind(ListModel data){
            this.data = data;
            textlocation.setText("Location:  " + data.getlocation());
            textpublish.setText("Publish:  " + data.getpublish());
            if (data.getlocation() != null && !data.getlocation().isEmpty()) {
                Picasso.get()
                        .load("file:///android_asset/" + data.getlocation() + ".png")
                        .into(img_country);
            } else {
                Picasso.get()
                        .load(R.drawable.earth)
                        .into(img_country);
            }
        }

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textlocation = itemView.findViewById(R.id.location);
            textpublish = itemView.findViewById(R.id.publish);
            textrandom = itemView.findViewById(R.id.random);
            btn_telegram = itemView.findViewById(R.id.btn_teleg);
            img_country = itemView.findViewById(R.id.flag);
            btn_share = itemView.findViewById(R.id.btn_share);
            statusColor = itemView.findViewById(R.id.sts_color);

            btn_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shareProxy(data);
                }
            });

            btn_telegram.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListner.Onclick(data);
                }
            });

            btn_telegram.setText(context.getString(R.string.OffcialTel));
        }
    }

    private void speed(final ViewHolder holder) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int[] pNom = context.getResources().getIntArray(R.array.androidcolors);
                int randompNom = pNom[new Random().nextInt(pNom.length)];
                holder.textrandom.setText(String.valueOf("Speed:  " + randompNom + " ms"));
                if (randompNom == 0) {
                    holder.statusColor.setImageDrawable(context.getResources().getDrawable(R.drawable.nav));
                } else {
                    holder.statusColor.setImageDrawable(context.getResources().getDrawable(R.drawable.av));

                }
            }
        }, 3000);
    }

    private void shareProxy(ListModel data) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        String introduce = context.getResources().getString(R.string.listShareMessage);
        introduce = introduce + "https://play.google.com/store/apps/details?id=" + context.getPackageName() + "\n\n";
        sendIntent.putExtra(Intent.EXTRA_TEXT, context.getResources().getString(R.string.useProxy)+ data.getlink() + introduce);
        context.startActivity(sendIntent.setFlags(FLAG_ACTIVITY_NEW_TASK));
    }
}