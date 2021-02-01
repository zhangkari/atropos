package com.class100.atropos.env.context;

import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.StringRes;

public final class AtViewPieces extends AtContextAbility {
    private final View root;
    private SparseArray<View> cache = new SparseArray<>(64);

    public AtViewPieces(View root) {
        this.root = root;
    }

    @SuppressWarnings("unchecked")
    public <T extends View> T find(int id) {
        View view = cache.get(id);
        if (view != null) {
            return (T) view;
        }
        T t = root.findViewById(id);
        cache.put(id, t);
        return t;
    }

    public AtViewPieces attach(@IdRes int parent, @LayoutRes int child) {
        View.inflate(root.getContext(), child, (ViewGroup) find(parent));
        return this;
    }

    public AtViewPieces setText(@IdRes int id, @StringRes int strId) {
        TextView tv = find(id);
        tv.setText(strId);
        return this;
    }

    public AtViewPieces setText(@IdRes int id, CharSequence text) {
        TextView tv = find(id);
        tv.setText(text);
        return this;
    }

    public AtViewPieces setVisibility(@IdRes int id, int visibility) {
        find(id).setVisibility(visibility);
        return this;
    }
}
