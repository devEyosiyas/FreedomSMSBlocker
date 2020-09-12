package dev.eyosiyas.smsblocker.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.mukesh.MarkdownView;

import dev.eyosiyas.smsblocker.R;

public class AboutFragment extends Fragment {

    public AboutFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        MarkdownView markdown = view.findViewById(R.id.markdown);
        markdown.loadMarkdownFromAssets("about.md");
        return view;
    }
}