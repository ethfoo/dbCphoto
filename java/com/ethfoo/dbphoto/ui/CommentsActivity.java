package com.ethfoo.dbphoto.ui;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ethfoo.dbphoto.R;
import com.ethfoo.dbphoto.data.CommentItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public class CommentsActivity extends ActionBarActivity {
    private static final String TAG = "CommentsActivity";
    public static final String COMMENTS_LIST = "comments list";

    private List<CommentItem> mCommentsList;

    private ListView listView;
    private ImageButton btnApply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        getSupportActionBar().setHomeButtonEnabled(true);

      //  mCommentsList = new ArrayList<>();
        mCommentsList = (ArrayList<CommentItem>) getIntent().getSerializableExtra(COMMENTS_LIST);

        btnApply = (ImageButton) findViewById(R.id.activity_comments_apply);
        btnApply.setImageResource(android.R.drawable.ic_media_play);


        listView = (ListView) findViewById(R.id.activity_comments_listView);
        CommentsAdapter adapter = new CommentsAdapter(this, mCommentsList);
        listView.setAdapter(adapter);

    }

    class CommentsAdapter extends ArrayAdapter<CommentItem>{

        public CommentsAdapter(Context context, List<CommentItem> objects) {
            super(context, 0, objects);

        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;
            if( convertView == null){
                holder = new ViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.activity_comments_comment_item, parent, false);
                holder.avatar = (ImageView) convertView.findViewById(R.id.activity_comments_item_comment_usr_image);
                holder.usrName = (TextView) convertView.findViewById(R.id.activity_comments_item_comment_user_tv);
                holder.content = (TextView) convertView.findViewById(R.id.activity_comments_item_comment_content);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }


            CommentItem item = getItem(position);

            Log.e(TAG,"position-->"+ position + "  item.getUserImageUrl()="+item.getUserImageUrl()+ "   item.getUserName()="+item.getUserName()+ "  item.getContent()="+item.getContent());

            Picasso.with(CommentsActivity.this).load(item.getUserImageUrl()).into(holder.avatar);
            holder.usrName.setText(item.getUserName());
            holder.content.setText(item.getContent());

            return convertView;
        }

        class ViewHolder{
            ImageView avatar;
            TextView usrName;
            TextView content;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_comments, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
