package com.kesa.members;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.kesa.R;
import com.kesa.app.KesaApplication;
import com.kesa.profile.ProfileActivity;
import com.kesa.profile.User;
import com.kesa.profile.UserManager;

import javax.inject.Inject;

import butterknife.ButterKnife;

/**
 * A fragment displaying the list of executive members of the {@link KesaApplication}.
 *
 * @author hongil
 */
public class ExecutiveMemberFragment extends Fragment {

    @Inject UserManager userManager;
    @Inject ExecutiveMemberExpandableListAdapter executiveMemberExpandableListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Dependency Injection
        ((KesaApplication) getActivity().getApplication()).getComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(
        LayoutInflater inflater,
        @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_expandable_listview, container, false);

        // Setting up ExpandableListView with executive members' data.
        ExpandableListView expandableListView = ButterKnife.findById(view, android.R.id.list);
        expandableListView.setAdapter(executiveMemberExpandableListAdapter);
        expandableListView.setOnChildClickListener(
            new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(
                    ExpandableListView parent,
                    View v,
                    int groupPosition,
                    int childPosition,
                    long id) {

                    User currentUser = (User) executiveMemberExpandableListAdapter.getChild(
                        groupPosition, childPosition);
                    Intent profileIntent = new Intent(getActivity(), ProfileActivity.class);
                    profileIntent.putExtra(ProfileActivity.USER_UID, currentUser.getUid());
                    getActivity().startActivity(profileIntent);
                    return true;
                }
            });
//        userManager.getExecutives(new Observer<User>() {
//            @Override
//            public void onCompleted() {
//
//            }
//
//            @Override
//            public void onError(Throwable e) {
//
//            }
//
//            @Override
//            public void onNext(User user) {
//                if (user != null) {
//                    executiveMemberExpandableListAdapter.insertItem(user);
//                }
//            }
//        });

        return view;
    }
}
