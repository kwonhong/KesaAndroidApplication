//package com.kesa.members;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.design.widget.TabLayout;
//import android.support.v4.app.Fragment;
//import android.support.v4.view.ViewPager;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.MenuInflater;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.ViewGroup;
//
//import com.google.common.collect.ImmutableList;
//import com.kesa.R;
//import com.kesa.SearchActivity;
//import com.kesa.app.KesaApplication;
//
//import java.util.List;
//
//import butterknife.ButterKnife;
//
///**
// * A fragment that displays the members of the {@link KesaApplication}.
// *
// * @author hongil
// */
//public class MembersFragment extends Fragment {
//
//    /** Fragments that are displayed in the tab layout. */
//    private static final List<Fragment> FRAGMENTS =
//        ImmutableList.of(new ExecutiveMemberFragment(), new NormalMemberFragment());
//    private static final List<String> FRAGMENT_TITLES =
//        ImmutableList.of("Execs", "Members");
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        // Dependency Injection
//        ((KesaApplication) getActivity().getApplication()).getComponent().inject(this);
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(
//        final LayoutInflater inflater,
//        @Nullable ViewGroup container,
//        @Nullable Bundle savedInstanceState) {
//        setHasOptionsMenu(true); // Allowing the fragment to have its own actionBar menus.
//
//        View view = inflater.inflate(R.layout.fragment_members, container, false);
//        final TabLayout tabLayout = ButterKnife.findById(view, R.id.tabLayout);
//        final ViewPager viewPager = ButterKnife.findById(view, R.id.viewPager);
//
//        viewPager.setAdapter(
//            new MemberFragmentPagerAdapter(getChildFragmentManager())
//                .setFragments(FRAGMENTS)
//                .setFragmentTitles(FRAGMENT_TITLES));
//        tabLayout.setupWithViewPager(viewPager);
//
//        return view;
//    }
//
//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//        inflater.inflate(R.menu.menu_members, menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_search:
//                Intent searchMemberIntent = new Intent(getActivity(), SearchActivity.class);
//                startActivity(searchMemberIntent);
//                return true;
//
//            default:
//                return false;
//        }
//    }
//}
