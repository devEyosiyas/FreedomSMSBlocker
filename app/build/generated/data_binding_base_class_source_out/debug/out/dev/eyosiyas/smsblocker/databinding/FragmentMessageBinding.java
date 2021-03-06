// Generated by view binder compiler. Do not edit!
package dev.eyosiyas.smsblocker.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import dev.eyosiyas.smsblocker.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class FragmentMessageBinding implements ViewBinding {
  @NonNull
  private final FrameLayout rootView;

  @NonNull
  public final FloatingActionButton fabSendMessage;

  @NonNull
  public final RecyclerView smsListRecycler;

  private FragmentMessageBinding(@NonNull FrameLayout rootView,
      @NonNull FloatingActionButton fabSendMessage, @NonNull RecyclerView smsListRecycler) {
    this.rootView = rootView;
    this.fabSendMessage = fabSendMessage;
    this.smsListRecycler = smsListRecycler;
  }

  @Override
  @NonNull
  public FrameLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static FragmentMessageBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FragmentMessageBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.fragment_message, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FragmentMessageBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.fabSendMessage;
      FloatingActionButton fabSendMessage = rootView.findViewById(id);
      if (fabSendMessage == null) {
        break missingId;
      }

      id = R.id.smsListRecycler;
      RecyclerView smsListRecycler = rootView.findViewById(id);
      if (smsListRecycler == null) {
        break missingId;
      }

      return new FragmentMessageBinding((FrameLayout) rootView, fabSendMessage, smsListRecycler);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
