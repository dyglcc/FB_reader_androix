package org.geometerplus.android.fbreader.library;

import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.dyg.android.reader.R;

import org.geometerplus.android.fbreader.FBReader;
import org.geometerplus.android.fbreader.libraryService.BookCollectionShadow;
import org.geometerplus.fbreader.book.Book;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

import dyg.activity.FbDefaultActivity;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static dyg.activity.FbDefaultActivity.PATH_BOOKS;
import static dyg.activity.FbDefaultActivity.PATH_ICONS;

public class DefaultBooks {
    private static final String TAG = "defaut_book";
    WeakReference<FbDefaultActivity> activityWeakReference = null;
    String[] strings = null;
    String[] icons = null;
    private BookCollectionShadow myCollection = new BookCollectionShadow();
    DefaultBooksAdapter adapter;

    public DefaultBooks(final FbDefaultActivity defaultBooksActivity) {
        activityWeakReference = new WeakReference<>(defaultBooksActivity);
        File dirBooks = new File(defaultBooksActivity.getFilesDir(), PATH_BOOKS);
        File dirIcons = new File(defaultBooksActivity.getFilesDir(), PATH_ICONS);
        if (dirBooks.exists() && dirIcons.exists()) {
            strings = dirBooks.list();
            icons = dirIcons.list();
        } else {
            Log.e(TAG, "DefaultBooks default file is empty");
            return;
        }
        Log.e(TAG, "DefaultBooks: ");
        adapter = new DefaultBooksAdapter();
        defaultBooksActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final RecyclerView recyclerView = defaultBooksActivity.findViewById(R.id.layout_default_books);
                recyclerView.setAdapter(adapter);
                final GridLayoutManager gridLayoutManager = new GridLayoutManager(defaultBooksActivity, 2);
                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return adapter.isHeader(position) ? 2 : 1;
                    }
                });
                recyclerView.setLayoutManager(gridLayoutManager);
                adapter.setOnItemClickListener(new ItemClick(myCollection, strings, defaultBooksActivity));
//                // 显示界面并展示蒙层引导
                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (recyclerView.getChildCount() > 2) {
                            View childViewSec = recyclerView.getChildAt(2);
                            if (activityWeakReference.get() != null) {
                                activityWeakReference.get().showGuideView(childViewSec);
                            }
                        }
                    }
                }, 300);

            }
        });

    }

    private final static class VhDefault extends RecyclerView.ViewHolder {
        TextView view;
        ImageView imageView;

        public VhDefault(@NonNull View itemView) {
            super(itemView);
            view = itemView.findViewById(R.id.tv_book_name);
            imageView = itemView.findViewById(R.id.iv_img);
        }
    }

    private final static class HeaderVH extends RecyclerView.ViewHolder {
        public HeaderVH(@NonNull View itemView) {
            super(itemView);
        }
    }

    private interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    private final static class ItemClick implements OnItemClickListener {
        //        DefaultBooksAdapter adapter;
        WeakReference<BookCollectionShadow> myCollection;
        String[] strings = null;
        WeakReference<FbDefaultActivity> defaultBooksActivity = null;

        ItemClick(BookCollectionShadow myCollection, String[] datas, FbDefaultActivity activity) {
            this.myCollection = new WeakReference<>(myCollection);
            this.strings = datas;
            this.defaultBooksActivity = new WeakReference<>(activity);
        }

        @Override
        public void onItemClick(View view, final int position) {
            Log.e(TAG, "onItemClick: " + position);
            try {
                if (defaultBooksActivity.get() != null) {
                    if (myCollection.get() == null) return;
                    myCollection.get().bindToService(defaultBooksActivity.get(), new Runnable() {
                        public void run() {
                            try {
                                final String name = strings[position];
                                Book book = myCollection.get().getBookByFile(defaultBooksActivity.get().getFilesDir()
                                        + "/" + PATH_BOOKS + "/"
                                        + name);
                                FBReader.openBookActivity(defaultBooksActivity.get(), book, null);
                                if (defaultBooksActivity != null && defaultBooksActivity.get() != null && !defaultBooksActivity.get().isFinishing()) {
                                    defaultBooksActivity.get().finish();
                                }

                            } catch (Throwable e) {
                                e.printStackTrace();
                            }

                        }
                    });
                }

            } catch (Exception t) {
                t.printStackTrace();
            }
        }
    }

    private class DefaultBooksAdapter extends RecyclerView.Adapter implements View.OnClickListener {


        private static final int HEADER = 1;
        private static final int NORMAL = 2;
        private ItemClick onItemclickListener;

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == HEADER) {
                View viewHeader = LayoutInflater.from(activityWeakReference.get()).inflate(R.layout.grid_head, null, false);
                return new HeaderVH(viewHeader);
            } else {
                View convertView = LayoutInflater.from(activityWeakReference.get()).inflate(R.layout.item_default_book, null, false);
                convertView.setOnClickListener(this);
                return new VhDefault(convertView);
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return HEADER;
            } else {
                return NORMAL;
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder1, int position) {
            try {
                // set textView
                if (holder1 instanceof VhDefault) {
                    position = position - 1;
                    String name = strings[position];
                    String picPath = findPicPath(name);
                    VhDefault holder = (VhDefault) holder1;
                    holder.itemView.setTag(position);
                    holder.view.setText(name.substring(0, name.indexOf(".")).intern());

                    if (picPath != null) {
                        File file = new File(activityWeakReference.get().getFilesDir() + "/" + PATH_ICONS + "/" + picPath);
                        holder.imageView.setImageBitmap(BitmapFactory.decodeStream(new FileInputStream(file)));
                        Glide.with(activityWeakReference
                                .get())
                                .load(file)
                                .transition(withCrossFade())
                                .apply(new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(25)))
                                .into(holder.imageView);
                    } else {
                        holder.imageView.setImageDrawable(null);
                    }
                }


            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        @Override
        public int getItemCount() {
            return strings == null ? 0 : strings.length + 1;
        }

        @Override
        public void onClick(View view) {
            if (onItemclickListener != null) {
                onItemclickListener.onItemClick(view, (int) view.getTag());
            }
        }

        public void setOnItemClickListener(ItemClick clickListener) {
            this.onItemclickListener = clickListener;
        }

        public boolean isHeader(int position) {
            if (position == 0) {
                return true;
            } else {
                return false;
            }
        }
    }

    private String findPicPath(String name) {
        String[] picNames = name.split("\\.");
        String picName = picNames[0];
        for (int i = 0; i < icons.length; i++) {
            if (icons[i].startsWith(picName)) {
                return icons[i];
            }
        }
        return null;
    }
}
