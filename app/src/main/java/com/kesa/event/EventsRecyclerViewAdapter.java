package com.kesa.event;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kesa.R;
import com.kesa.util.ImageManager;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * An adapter responsible for displaying the events.
 *
 * @author hongil
 */
public class EventsRecyclerViewAdapter
    extends RecyclerView.Adapter<EventsRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private List<Event> events;
    private ImageManager imageManager;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        ImageView imageView;
        TextView titleTextView;
        TextView dayTextView;
        TextView monthTextView;
        TextView locationTextView;
        TextView timeTextView;
        View.OnClickListener onClickListener;

        public ViewHolder(
            View itemView,
            TextView titleTextView,
            TextView dayTextView,
            TextView monthTextView,
            TextView locationTextView,
            TextView timeTextView,
            ImageView imageView) {
            super(itemView);
            this.titleTextView = titleTextView;
            this.dayTextView = dayTextView;
            this.monthTextView = monthTextView;
            this.locationTextView = locationTextView;
            this.timeTextView = timeTextView;
            this.imageView = imageView;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onClickListener.onClick(v);
        }
    }

    @Inject
    public EventsRecyclerViewAdapter(ImageManager imageManager) {
        this.imageManager = imageManager;
        events = new ArrayList<>();
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    @Override
    public EventsRecyclerViewAdapter.ViewHolder onCreateViewHolder(
        final ViewGroup parent, int viewType) {
        // create a new view
        this.context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.recycler_item_event, parent, false);

        TextView titleTextView = (TextView) view.findViewById(R.id.titleTextView);
        TextView dayTextView = (TextView) view.findViewById(R.id.dayTextView);
        TextView monthTextView = (TextView) view.findViewById(R.id.monthTextView);
        TextView locationTextView = (TextView) view.findViewById(R.id.locationTextView);
        TextView timeTextView = (TextView) view.findViewById(R.id.timeTextView);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);

        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder(
            view,
            titleTextView,
            dayTextView,
            monthTextView,
            locationTextView,
            timeTextView,
            imageView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Event currentEvent = events.get(position);
        holder.titleTextView.setText(currentEvent.getTitle());
        holder.locationTextView.setText(currentEvent.getLocation());

        DateTime eventTime = new DateTime(currentEvent.getTimestamp());
        holder.dayTextView.setText(eventTime.toString("dd"));
        holder.monthTextView.setText(eventTime.toString("MMM"));

        String time = eventTime.dayOfWeek().getAsText() + " " + eventTime.toString("HH:mm");
        holder.timeTextView.setText(time);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return events.size();
    }

    /** Inserts the given {@code user} in the member list. */
    public void insertItem(Event event) {
        events.add(events.size(), event);
    }

    /** Clears the data in {@code events} */
    public void clear() {
        events.clear();
        notifyDataSetChanged();
    }
}
