package com.hurhco.telegramPro;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.yandex.metrica.YandexMetrica;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class ProItemAdapter extends RecyclerView.Adapter<ProItemAdapter.ViewHolder> {

    private Context context;
    private List<ProList> proList;
    private SharedPreferences sharedPreferences;
    private IClickListner clickListner;

    //todo
    public ProItemAdapter(Context context, List<ProList> list, IClickListner clickListner) {
        this.context = context;
        this.proList = list;
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
        return proList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        ping(holder);
        holder.bind(proList.get(position));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        protected Button btn_telegram;
        protected ImageButton btn_share;
        public ImageView img_country;
        public TextView textpublish, textlocation, textrandom;
        private LinearLayout statusColor;
        private ProList data;

        public void bind(ProList data){
            this.data = data;
            textlocation.setText("Location:  " + data.getlocation());
            textpublish.setText("Publish:  " + data.getpublish());
            if (data.getImgc() != null && !data.getImgc().isEmpty()) {
                Picasso.get()
                        .load("file:///android_asset/" + data.getImgc())
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
            img_country = itemView.findViewById(R.id.imgc);
            btn_share = itemView.findViewById(R.id.btn_share);
            statusColor = itemView.findViewById(R.id.sts_color);

            Typeface typefacepublish = Typeface.createFromAsset(textpublish.getContext().getAssets(), "font/appfontlgt.ttf");
            textpublish.setTypeface(typefacepublish);
            Typeface typefacelocation = Typeface.createFromAsset(textlocation.getContext().getAssets(), "font/appfontlgt.ttf");
            textlocation.setTypeface(typefacelocation);
            Typeface typefacerandom = Typeface.createFromAsset(textrandom.getContext().getAssets(), "font/appfontlgt.ttf");
            textrandom.setTypeface(typefacerandom);
            Typeface telbtnfont = Typeface.createFromAsset(context.getAssets(), "font/appfontmed.ttf");
            btn_telegram.setTypeface(telbtnfont);


            btn_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    proxyListEvent(1);
                    shareProxy(data);
                }
            });

            btn_telegram.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    proxyListEvent(0);
                    clickListner.Onclick(data);
                }
            });

            btn_telegram.setText(context.getString(R.string.OffcialTel));
        }
    }

    private void ping(final ViewHolder holder) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int[] pNom = context.getResources().getIntArray(R.array.androidcolors);
                int randompNom = pNom[new Random().nextInt(pNom.length)];
                holder.textrandom.setText(String.valueOf("Speed:  " + randompNom + " ms"));
                if (randompNom == 0) {
                    holder.statusColor.setBackgroundColor(context.getResources().getColor(R.color.NotAvaliable));
                } else {
                    holder.statusColor.setBackgroundColor(context.getResources().getColor(R.color.Avaliable));

                }
            }
        }, 3000);
    }

    private void shareProxy(ProList data) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        String introduce = context.getResources().getString(R.string.moreProxy);
        introduce = introduce + "https://play.google.com/store/apps/details?id=" + context.getPackageName() + "\n\n";
        sendIntent.putExtra(Intent.EXTRA_TEXT, context.getResources().getString(R.string.useProxy)+ data.getlipk() + introduce);
        context.startActivity(sendIntent.setFlags(FLAG_ACTIVITY_NEW_TASK));
    }

    private void proxyListEvent(int staus) {
        Map<String, Object> eventParameters = new HashMap<String, Object>();

        switch (staus) {
            case 0:
                eventParameters.put("Connect Button", "Touched");
                break;
            case 1:
                eventParameters.put("Share Button", "Touched");
                break;
        }
        YandexMetrica.reportEvent("List Activity", eventParameters);
    }
}