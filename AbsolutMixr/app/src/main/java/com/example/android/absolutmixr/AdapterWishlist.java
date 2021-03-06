package com.example.android.absolutmixr;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.absolutmixr.Model.DrinkItem;
import com.example.android.absolutmixr.Model.WishlistContract;
import com.example.android.absolutmixr.Model.WishlistDbHelper;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.android.absolutmixr.AdapterDrink.thumbsRatingDown;
import static com.example.android.absolutmixr.AdapterDrink.thumbsRatingNone;
import static com.example.android.absolutmixr.AdapterDrink.thumbsRatingUp;

/**
 * Created by melaniekwon on 8/6/17.
 */

public class AdapterWishlist extends RecyclerView.Adapter<AdapterWishlist.WishlistViewHolder> {
    private static final String TAG = AdapterWishlist.class.getSimpleName();

    private Context mContext;
    private Cursor mCursor;
    private SQLiteDatabase mDb;

    public AdapterWishlist(Context mContext, Cursor mCursor) {
        this.mContext = mContext;
        this.mCursor = mCursor;
    }

    public class WishlistViewHolder extends RecyclerView.ViewHolder{
        public final TextView mDrinkName;
        public final TextView mTastes;
        public final ImageView mDrinkpic;
        public final Button mDotButton;
        public final ImageButton mThumbsupButton;
        public final ImageButton mThumbsdownButton;
        public final CardView mCardview;

        public WishlistViewHolder(View view){
            super(view);
            mDrinkName = (TextView) view.findViewById(R.id.wishlist_name);
            mTastes = (TextView) view.findViewById(R.id.wishlist_tastes);
            mDrinkpic = (ImageView) view.findViewById(R.id.wishlist_drinkImage);
            mDotButton = (Button) view.findViewById(R.id.wishlist_action);
            mThumbsupButton = (ImageButton) view.findViewById(R.id.wishlist_thumbsup);
            mThumbsdownButton = (ImageButton) view.findViewById(R.id.wishlist_thumbsdown);
            mCardview = (CardView) view.findViewById(R.id.wishlist_card_view);
        }
    }

    @Override
    public WishlistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Database for writing to Wishlist
        WishlistDbHelper dbHelper = new WishlistDbHelper(mContext);
        mDb = dbHelper.getWritableDatabase();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.display_wishlist_view, parent, false);
        return new WishlistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final WishlistViewHolder holder, final int position) {
        if (!mCursor.moveToPosition(position)) { return; }

        final String id = mCursor.getString(mCursor.getColumnIndex(WishlistContract.WishlistEntry._ID));
        final String name = mCursor.getString(mCursor.getColumnIndex(WishlistContract.WishlistEntry.COLUMN_NAME));
        final String description = mCursor.getString(mCursor.getColumnIndex(WishlistContract.WishlistEntry.COLUMN_DESCRIPTION));
        final String color = mCursor.getString(mCursor.getColumnIndex(WishlistContract.WishlistEntry.COLUMN_COLOR));
        final String skill = mCursor.getString(mCursor.getColumnIndex(WishlistContract.WishlistEntry.COLUMN_SKILL));
        final String rating = mCursor.getString(mCursor.getColumnIndex(WishlistContract.WishlistEntry.COLUMN_RATING));
        final String pic_URL = mCursor.getString(mCursor.getColumnIndex(WishlistContract.WishlistEntry.COLUMN_PICTURE_URL));
        final String thumbsup = mCursor.getString(mCursor.getColumnIndex(WishlistContract.WishlistEntry.COLUMN_THUMBSUP));

        // Strings are parsed into ArrayList<String>
        String ingredients = mCursor.getString(mCursor.getColumnIndex(WishlistContract.WishlistEntry.COLUMN_INGREDIENTS));
        String tastes = mCursor.getString(mCursor.getColumnIndex(WishlistContract.WishlistEntry.COLUMN_TASTES));
        String occassions = mCursor.getString(mCursor.getColumnIndex(WishlistContract.WishlistEntry.COLUMN_OCCASSIONS));
        final List<String> ingredientsList = Arrays.asList(ingredients.split("\\s*,\\s*"));
        final List<String> tastesList = Arrays.asList(tastes.split("\\s*,\\s*"));
        final List<String> occassionsList = Arrays.asList(occassions.split("\\s*,\\s*"));

        holder.mDrinkName.setText(name);
        holder.mTastes.setText(tastes);
        if(pic_URL != null){
            Picasso.with(mContext)
                    .load(pic_URL)
                    .into(holder.mDrinkpic);
        }

        holder.itemView.setTag(id);

        final Drawable defaultThumbsup = mContext.getResources().getDrawable(R.drawable.ic_thumb_up_black_24dp);
        final Drawable selectedThumbsup = mContext.getResources().getDrawable(R.drawable.ic_thumb_up_selected);
        final Drawable defaultThumbsdown = mContext.getResources().getDrawable(R.drawable.ic_thumb_down_black_24dp);
        final Drawable selectedThumbsdown = mContext.getResources().getDrawable(R.drawable.ic_thumb_down_selected);

        if (thumbsup.equals(thumbsRatingUp)) {
            holder.mThumbsupButton.setImageDrawable(selectedThumbsup);
        } else if (thumbsup.equals(thumbsRatingDown)) {
            holder.mThumbsdownButton.setImageDrawable(selectedThumbsdown);
        }

        // TODO: share picture with tweet
        holder.mDotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(mContext, holder.mDotButton);
                popupMenu.getMenuInflater().inflate(R.menu.wishlist_popup_action, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        int menuId = item.getItemId();
                        switch(menuId) {
//                            case R.id.wishlist_action_photo:
//                                hasPermissionInManifest(mContext, Manifest.permission.CAMERA);
//                                ((MainActivity)mContext).dispatchTakePictureIntent();
//                                ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.viewpager);
//                                return true;
                            case R.id.wishlist_action_share:
//                                ((MainActivity)mContext).dispatchChoosePictureIntent();
                                String tweet = "Enjoying my refreshing #" + name + "! Thanks @AbsolutMixr!";
//                                Uri myUri = Uri.parse(pic_URL);
                                TweetComposer.Builder builder = new TweetComposer.Builder(mContext)
//                                        .image(myUri)
                                        .text(tweet);
                                builder.show();
                                return true;
                            default:
                                return false;
                        }
                    }
                });

                popupMenu.show();
            }
        });

        // TODO: FIX LOGIC
        holder.mThumbsupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "thumbsup: " + thumbsRatingUp);
                switch (thumbsup) {
                    case thumbsRatingUp:
                        updateThumbsRating(id, thumbsRatingNone);
                        holder.mThumbsupButton.setImageDrawable(defaultThumbsup);
                        Log.d(TAG, "switching up to none");
                        break;
                    case thumbsRatingDown:
                        updateThumbsRating(id, thumbsRatingUp);
                        holder.mThumbsupButton.setImageDrawable(selectedThumbsup);
                        holder.mThumbsdownButton.setImageDrawable(defaultThumbsdown);
                        Log.d(TAG, "switching down to none");
                        break;
                    case thumbsRatingNone:
                        updateThumbsRating(id, thumbsRatingUp);
                        holder.mThumbsupButton.setImageDrawable(selectedThumbsup);
                        holder.mThumbsdownButton.setImageDrawable(defaultThumbsdown);
                        Log.d(TAG, "switching none to up");
                        break;
                    default:
                        Log.d(TAG, "nothing happened.");
                        break;
                }
            }
        });

        holder.mThumbsdownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateThumbsRating(id, thumbsRatingDown);
                Log.d(TAG, "thumbsup: " + thumbsRatingDown);

                switch (thumbsup) {
                    case thumbsRatingUp:
                        updateThumbsRating(id, thumbsRatingDown);
                        holder.mThumbsdownButton.setImageDrawable(selectedThumbsdown);
                        holder.mThumbsupButton.setImageDrawable(defaultThumbsup);
                        Log.d(TAG, "switching up to none");
                        break;
                    case thumbsRatingDown:
                        updateThumbsRating(id, thumbsRatingNone);
                        holder.mThumbsdownButton.setImageDrawable(defaultThumbsdown);
                        Log.d(TAG, "switching down to none");
                        break;
                    case thumbsRatingNone:
                        updateThumbsRating(id, thumbsRatingDown);
                        holder.mThumbsdownButton.setImageDrawable(selectedThumbsdown);
                        holder.mThumbsupButton.setImageDrawable(defaultThumbsup);
                        Log.d(TAG, "switching none to down");
                        break;
                    default:
                        Log.d(TAG, "nothing happened.");
                        break;
                }
            }
        });

        //making the cardview section clickable to see drink details
        holder.mCardview.setOnClickListener(new View.OnClickListener() {
            // NOTE: id of the DrinkItem is used to call the picture
            String test = "test";
            ArrayList<String> testArray = new ArrayList<String>(Arrays.asList(test, test, test));
            DrinkItem drinkItem = new DrinkItem(id, name, description, color, skill, rating, ingredientsList, occassionsList, tastesList);

            @Override
            public void onClick(View v) {
//                Log.d(TAG, "Clicked on " + drinkcount.getName() + " info");
                Intent intent = new Intent(mContext,DetailDrink.class);
                intent.putExtra("Drink Object",drinkItem);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor){
        if (mCursor != null) mCursor.close();
        mCursor = newCursor;
        if (newCursor != null) {
            this.notifyDataSetChanged();
        }
    }

    private int updateThumbsRating(String id, String status) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(WishlistContract.WishlistEntry.COLUMN_THUMBSUP, status);

        // Which row to update, based on the title
        String selection = WishlistContract.WishlistEntry._ID + " LIKE ?";
        String[] selectionArgs = { id };

        return mDb.update(
                WishlistContract.WishlistEntry.TABLE_NAME,
                contentValues,
                selection,
                selectionArgs);

    }

    // Needed to access Camera
    // SOURCE: https://stackoverflow.com/questions/32789027/android-m-camera-intent-permission-bug
    public boolean hasPermissionInManifest(Context context, String permissionName) {
        final String packageName = context.getPackageName();
        try {
            final PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
            final String[] declaredPermisisons = packageInfo.requestedPermissions;
            if (declaredPermisisons != null && declaredPermisisons.length > 0) {
                for (String p : declaredPermisisons) {
                    if (p.equals(permissionName)) {
                        return true;
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {

        }
        return false;
    }
}
