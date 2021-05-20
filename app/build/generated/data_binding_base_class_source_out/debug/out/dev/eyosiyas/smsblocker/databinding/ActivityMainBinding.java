// Generated by view binder compiler. Do not edit!
package dev.eyosiyas.smsblocker.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentContainerView;
import androidx.viewbinding.ViewBinding;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import dev.eyosiyas.smsblocker.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityMainBinding implements ViewBinding {
  @NonNull
  private final DrawerLayout rootView;

  @NonNull
  public final AppBarLayout appBar;

  @NonNull
  public final BottomNavigationView bottomNavView;

  @NonNull
  public final DrawerLayout drawer;

  @NonNull
  public final FragmentContainerView fragmentHost;

  @NonNull
  public final NavigationView navigationView;

  @NonNull
  public final MaterialToolbar toolbar;

  private ActivityMainBinding(@NonNull DrawerLayout rootView, @NonNull AppBarLayout appBar,
      @NonNull BottomNavigationView bottomNavView, @NonNull DrawerLayout drawer,
      @NonNull FragmentContainerView fragmentHost, @NonNull NavigationView navigationView,
      @NonNull MaterialToolbar toolbar) {
    this.rootView = rootView;
    this.appBar = appBar;
    this.bottomNavView = bottomNavView;
    this.drawer = drawer;
    this.fragmentHost = fragmentHost;
    this.navigationView = navigationView;
    this.toolbar = toolbar;
  }

  @Override
  @NonNull
  public DrawerLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityMainBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityMainBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_main, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityMainBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.appBar;
      AppBarLayout appBar = rootView.findViewById(id);
      if (appBar == null) {
        break missingId;
      }

      id = R.id.bottomNavView;
      BottomNavigationView bottomNavView = rootView.findViewById(id);
      if (bottomNavView == null) {
        break missingId;
      }

      DrawerLayout drawer = (DrawerLayout) rootView;

      id = R.id.fragmentHost;
      FragmentContainerView fragmentHost = rootView.findViewById(id);
      if (fragmentHost == null) {
        break missingId;
      }

      id = R.id.navigationView;
      NavigationView navigationView = rootView.findViewById(id);
      if (navigationView == null) {
        break missingId;
      }

      id = R.id.toolbar;
      MaterialToolbar toolbar = rootView.findViewById(id);
      if (toolbar == null) {
        break missingId;
      }

      return new ActivityMainBinding((DrawerLayout) rootView, appBar, bottomNavView, drawer,
          fragmentHost, navigationView, toolbar);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
