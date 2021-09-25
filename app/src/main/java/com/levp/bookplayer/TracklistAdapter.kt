package com.levp.bookplayer

import android.net.wifi.ScanResult
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.levp.bookplayer.util.TrackSupport

class TracklistAdapter(private val trackList: ArrayList<TrackSupport.Track>) : RecyclerView.Adapter<TracklistAdapter.TracklistViewHolder>() {

    class TracklistViewHolder(view: View): RecyclerView.ViewHolder(view){
        var trackName: TextView = view.findViewById(R.id.trackitem_track_name) as TextView
        var trackArtist: TextView = view.findViewById(R.id.trackitem_track_artist) as TextView
        var trackCover = view.findViewById(R.id.trackitem_cover_img) as ImageView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracklistAdapter.TracklistViewHolder {
        return TracklistViewHolder( LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false))
    }

    override fun onBindViewHolder(holder: TracklistAdapter.TracklistViewHolder, position: Int) {
        holder.trackName.text =  trackList[position].name
        holder.trackArtist.text =  trackList[position].artist
        //holder.trackCover.clipToOutline = true
//        if(trackList[position].imageUri!=null)
//        {
//            Glide.with(holder.trackCover)
//                .asBitmap()
//                .load(trackList[position].imageUri)
//                .into(holder.trackCover)
//        }

    }

    override fun getItemCount(): Int {
        return trackList.size
    }

    //TODO fucker
    fun updateData(scanResult: ArrayList<ScanResult>) {
        trackList.clear()
        notifyDataSetChanged()

    }
}