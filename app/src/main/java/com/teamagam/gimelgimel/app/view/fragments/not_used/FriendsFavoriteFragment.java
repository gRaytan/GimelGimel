package com.teamagam.gimelgimel.app.view.fragments.not_used;

import com.teamagam.gimelgimel.app.view.fragments.FriendsFragment;

/**
 * A fragment representing a list of favorite friends.
 */
//todo: no need for this fle
public class FriendsFavoriteFragment extends FriendsFragment {

    @Override
    protected String getFragmentSelection() {
        //TODO: clean
        return null;
//        return FriendsEntity.DB.IS_FAVORITE + "=?";
    }

    @Override
    protected String[] getFragmentSelectionArgs() {
        // "1" - represents true in ContentProvider
        return new String[]{"1"};
    }

    @Override
    public int getTitle() {
        //TODO: clean
        return -1;
//        return R.string.fragment_gimel_favorite_title;
    }
}