package com.peliculandia.pop.adapters;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.peliculandia.pop.DetailsActivity;
import com.peliculandia.pop.R;
import com.peliculandia.pop.models.EpiModel;
import com.peliculandia.pop.types.UrlMode;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class EpisodeAdapter extends RecyclerView.Adapter<EpisodeAdapter.OriginalViewHolder> {

    private List<EpiModel> items = new ArrayList<>();
    private Context ctx;
    final EpisodeAdapter.OriginalViewHolder[] viewHolderArray = {null};
    private OnTVSeriesEpisodeItemClickListener mOnTVSeriesEpisodeItemClickListener;
    EpisodeAdapter.OriginalViewHolder viewHolder;
    int i=0;
    private int seasonNo;

    public interface OnTVSeriesEpisodeItemClickListener {
        void onEpisodeItemClickTvSeries(String type, View view, EpiModel obj, int position, OriginalViewHolder holder);
    }

    public void setOnEmbedItemClickListener(OnTVSeriesEpisodeItemClickListener mItemClickListener) {
        this.mOnTVSeriesEpisodeItemClickListener = mItemClickListener;
    }

    public EpisodeAdapter(Context context, List<EpiModel> items) {
        this.items = items;
        ctx = context;
    }


    @Override
    public EpisodeAdapter.OriginalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        EpisodeAdapter.OriginalViewHolder vh;
        //View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_episode_item, parent, false);
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_episode_item_vertical, parent, false);
        vh = new EpisodeAdapter.OriginalViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final EpisodeAdapter.OriginalViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        final EpiModel obj = items.get(position);
        holder.name.setText(obj.getEpi());
        holder.seasonName.setText("Season: " + obj.getSeson());
        //holder.publishDate.setText(obj.);

        //check if isDark or not.
        //if not dark, change the text color
        SharedPreferences sharedPreferences = ctx.getSharedPreferences("push", MODE_PRIVATE);
        boolean isDark = sharedPreferences.getBoolean("dark", false);
        if (!isDark){
            holder.name.setTextColor(ctx.getResources().getColor(R.color.black));
            holder.seasonName.setTextColor(ctx.getResources().getColor(R.color.black));
            holder.publishDate.setTextColor(ctx.getResources().getColor(R.color.black));
        }

        Picasso.get()
                .load(obj.getImageUrl())
                .placeholder(R.drawable.poster_placeholder)
                .into(holder.episodIv);



        /*if (seasonNo == 0) {
            if (position==i){
                chanColor(viewHolderArray[0],position);
                ((DetailsActivity)ctx).setMediaUrlForTvSeries(obj.getStreamURL(), obj.getSeson(), obj.getEpi());
                new DetailsActivity().iniMoviePlayer(obj.getStreamURL(),obj.getServerType(),ctx);
                holder.name.setTextColor(ctx.getResources().getColor(R.color.colorPrimary));
                holder.playStatusTv.setText("Playing");
                holder.playStatusTv.setVisibility(View.VISIBLE);
                viewHolderArray[0] =holder;
                i = items.size()+items.size() + items.size();

            }
        }*/

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 Log.d("soy_una_verga","aqui es el click");
                ((DetailsActivity) ctx).hideDescriptionLayout();
                ((DetailsActivity) ctx).showSeriesLayout();
                ((DetailsActivity) ctx).setMediaUrlForTvSeries(obj.getStreamURL(), obj.getSeson(), obj.getEpi());

                ((DetailsActivity) ctx).txtalmacenaUrlSerie.setText(obj.getStreamURL()); //para enviarle los parametros desde el adaptador
               // txtalmacenaUrlSerie

                String almacenaUrl = obj.getStreamURL();

                String almacenaFuente = almacenaUrl.split("/")[2];


                if(almacenaFuente.equals("vidoza.net"))
                {
                    ((DetailsActivity) ctx).startWork(UrlMode.PLAY);;

                }else if(almacenaFuente.equals("mixdrop.to")){ //60dias inactivos revisado

                    ((DetailsActivity) ctx).startWork(UrlMode.PLAY);;
                }else if(almacenaFuente.equals("upstream.to")){

                    ((DetailsActivity) ctx).startWork(UrlMode.PLAY);;
                }else if(almacenaFuente.equals("cloudvideo.tv")){ //revisado

                    ((DetailsActivity) ctx).startWork(UrlMode.PLAY);;
                }else if(almacenaFuente.equals("jawcloud.co")){ //revisado

                    ((DetailsActivity) ctx).startWork(UrlMode.PLAY);;
                }else if(almacenaFuente.equals("www.veoh.com")){ //revisado

                    ((DetailsActivity) ctx).startWork(UrlMode.PLAY);;
                }else if(almacenaFuente.equals("dood.to")){ //60dias inactivos revisado

                    ((DetailsActivity) ctx).startWork(UrlMode.PLAY);;
                }else if(almacenaFuente.equals("dood.la")){ //60dias inactivos revisado

                    ((DetailsActivity) ctx).startWork(UrlMode.PLAY);;
                }else if(almacenaFuente.equals("doodstream.com")){ //60dias inactivos revisado

                    ((DetailsActivity) ctx).startWork(UrlMode.PLAY);;
                }else if(almacenaFuente.equals("uqload.com")){ //60dias inactivos revisado

                    ((DetailsActivity) ctx).startWork(UrlMode.PLAY);;
                }else if(almacenaFuente.equals("vidlox.me")){ //60dias inactivos revisado

                    ((DetailsActivity) ctx).startWork(UrlMode.PLAY);;
                }
                else
                {
                    ((DetailsActivity) ctx).abreSerieVideoPlayer();;
                    /*

                    Log.d("panda","no soy vidoza soy : "+almacenaFuente);
                    boolean castSession = ((DetailsActivity) ctx).getCastSession();
                    //Toast.makeText(ctx, "cast:"+castSession, Toast.LENGTH_SHORT).show();
                    if (!castSession) {
                        if (obj.getServerType().equalsIgnoreCase("embed")) {
                            if (mOnTVSeriesEpisodeItemClickListener != null) {
                                mOnTVSeriesEpisodeItemClickListener.onEpisodeItemClickTvSeries("embed", v, obj, position, viewHolder);
                            }
                        } else {
                            //new DetailsActivity().initMoviePlayer(obj.getStreamURL(), obj.getServerType(), ctx);
                            if (mOnTVSeriesEpisodeItemClickListener != null) {
                                Log.d("soy_una_verga","entre aqui al picar en series "+almacenaUrl);
                                mOnTVSeriesEpisodeItemClickListener.onEpisodeItemClickTvSeries("normal", v, obj, position, viewHolder);
                            }
                        }
                    } else {
                        ((DetailsActivity) ctx).showQueuePopup(ctx, holder.cardView, ((DetailsActivity) ctx).getMediaInfo());

                    }

                     */
                }

                chanColor(viewHolderArray[0],position);
                holder.name.setTextColor(ctx.getResources().getColor(R.color.colorPrimary));
                holder.playStatusTv.setText("Playing");
                holder.playStatusTv.setVisibility(View.VISIBLE);


                viewHolderArray[0] =holder;
            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {

        public TextView name, playStatusTv , seasonName, publishDate;
        public MaterialRippleLayout cardView;
        public ImageView episodIv;

        public OriginalViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.name);
            playStatusTv = v.findViewById(R.id.play_status_tv);
            cardView=v.findViewById(R.id.lyt_parent);
            episodIv=v.findViewById(R.id.image);
            seasonName = v.findViewById(R.id.season_name);
            publishDate = v.findViewById(R.id.publish_date);
        }
    }

    private void chanColor(EpisodeAdapter.OriginalViewHolder holder, int pos){

        if (holder!=null){
            holder.name.setTextColor(ctx.getResources().getColor(R.color.grey_20));
            holder.playStatusTv.setVisibility(View.GONE);
        }
    }


}