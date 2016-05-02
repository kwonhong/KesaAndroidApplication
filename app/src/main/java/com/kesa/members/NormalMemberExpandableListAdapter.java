//package com.kesa.members;
//
//import android.app.Application;
//import android.content.Context;
//import android.database.DataSetObserver;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseExpandableListAdapter;
//import android.widget.ExpandableListView;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.kesa.R;
//import com.kesa.app.KesaApplication;
//import com.kesa.user.User;
//import com.kesa.util.ImageEncoder;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import javax.inject.Inject;
//
//import butterknife.ButterKnife;
//
///**
// * An implementation of {@link BaseExpandableListAdapter} that is responsible for displaying
// * the members of the {@link KesaApplication}.
// *
// * @author hongil
// */
//public class NormalMemberExpandableListAdapter extends BaseExpandableListAdapter {
//
//    /** Provides a reference to the views for each child data item. */
//    class ChildViewHolder {
//
//        TextView nameTextView;
//        TextView programTextView;
//        ImageView profileImageView;
//
//        public ChildViewHolder(
//            TextView nameTextView,
//            TextView programTextView,
//            ImageView profileImageView) {
//
//            this.nameTextView = nameTextView;
//            this.programTextView = programTextView;
//            this.profileImageView = profileImageView;
//        }
//    }
//
//    /** Provides a reference to the views for each parent data item. */
//    class GroupViewHolder {
//
//        TextView labelTextView;
//
//        public GroupViewHolder(TextView labelTextView) {
//            this.labelTextView = labelTextView;
//        }
//    }
//
//    private final Map<Integer, List<User>> users;
//    private final List<Integer> admissionYears;
//    private final ImageEncoder imageEncoder;
//    private final Context context;
//
//    @Inject
//    public NormalMemberExpandableListAdapter(
//        final Application application,
//        final ImageEncoder imageEncoder) {
//
//        this.admissionYears = new ArrayList<>();
//        this.users = new HashMap<>();
//        this.context = application;
//        this.imageEncoder = imageEncoder;
//    }
//
//    @Override
//    public int getGroupCount() {
//        return admissionYears.size();
//    }
//
//    @Override
//    public int getChildrenCount(int groupPosition) {
//        return ((List) getGroup(groupPosition)).size();
//    }
//
//    @Override
//    public Object getGroup(int groupPosition) {
//        return users.get(admissionYears.get(groupPosition));
//    }
//
//    @Override
//    public Object getChild(int groupPosition, int childPosition) {
//        return ((List) getGroup(groupPosition)).get(childPosition);
//    }
//
//    @Override
//    public long getGroupId(int groupPosition) {
//        return groupPosition;
//    }
//
//    @Override
//    public long getChildId(int groupPosition, int childPosition) {
//        return childPosition;
//    }
//
//    @Override
//    public boolean hasStableIds() {
//        return false;
//    }
//
//    @Override
//    public View getGroupView(
//        int groupPosition,
//        boolean isExpanded,
//        View convertView,
//        ViewGroup parent) {
//
//        if (convertView == null) {
//            LayoutInflater layoutInflater =
//                (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            convertView = layoutInflater.inflate(R.layout.expandable_member_group_item, null);
//
//            TextView labelTextView = ButterKnife.findById(convertView, android.R.id.text1);
//            GroupViewHolder groupViewHolder = new GroupViewHolder(labelTextView);
//            convertView.setTag(groupViewHolder);
//        }
//
//        GroupViewHolder groupViewHolder = (GroupViewHolder) convertView.getTag();
//        groupViewHolder.labelTextView.setText(Integer.toString(admissionYears.get(groupPosition)));
//        convertView.setClickable(false);
//        convertView.setFocusable(false);
//        ((ExpandableListView) parent).expandGroup(groupPosition); // Expanding the group by default.
//        return convertView;
//    }
//
//    @Override
//    public View getChildView(
//        int groupPosition,
//        int childPosition,
//        boolean isLastChild,
//        View convertView,
//        ViewGroup parent) {
//
//        if (convertView == null) {
//            LayoutInflater layoutInflater =
//                (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            convertView = layoutInflater.inflate(R.layout.expandable_member_child_item, null);
//
//            TextView nameTextView = ButterKnife.findById(convertView, R.id.nameTextView);
//            TextView programTextView = ButterKnife.findById(convertView, R.id.programTextView);
//            ImageView profileImageView = ButterKnife.findById(convertView, R.id.profileImageView);
//            ChildViewHolder childViewHolder =
//                new ChildViewHolder(nameTextView, programTextView, profileImageView);
//            convertView.setTag(childViewHolder);
//        }
//
//        ChildViewHolder childViewHolder = (ChildViewHolder) convertView.getTag();
//
//        User currentUser = users.get(admissionYears.get(groupPosition)).get(childPosition);
//        childViewHolder.nameTextView.setText(currentUser.getName());
//        childViewHolder.programTextView.setText(currentUser.getProgram());
//        childViewHolder.profileImageView.setImageBitmap(
//            imageEncoder.decodeBase64(currentUser.getProfileImage()));
//        return convertView;
//    }
//
//    @Override
//    public void registerDataSetObserver(DataSetObserver observer) {
//        super.registerDataSetObserver(observer);
//    }
//
//    @Override
//    public boolean isChildSelectable(int groupPosition, int childPosition) {
//        return true; // Making the child items selectable
//    }
//
//    public void insertItem(User user) {
//        // TODO(hongil): Order by admissionYears.
//        int admissionYear = user.getAdmissionYear();
//        if (users.containsKey(admissionYear)) {
//            users.get(admissionYear).add(user);
//        } else {
//            List<User> userList = new ArrayList<>();
//            userList.add(user);
//            users.put(admissionYear, userList);
//            admissionYears.add(admissionYear);
//        }
//
//        notifyDataSetChanged();
//    }
//}
