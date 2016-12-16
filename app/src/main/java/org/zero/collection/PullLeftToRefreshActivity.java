package org.zero.collection;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.pulllefttorefresh.PullLeftToRefreshLayout;

public class PullLeftToRefreshActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pull_left_to_refresh);
        PullLeftToRefreshLayout plrl_sc = (PullLeftToRefreshLayout) findViewById(R.id.pltrl_sc);
        plrl_sc.setOnRefreshListener(new PullLeftToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(PullLeftToRefreshActivity.this, "刷新数据成功", Toast.LENGTH_SHORT).show();
            }
        });

        RecyclerView rv = (RecyclerView) findViewById(R.id.rv);
        rv.setAdapter(new PullAdapter());
        rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        PullLeftToRefreshLayout plrl = (PullLeftToRefreshLayout) findViewById(R.id.pltrl_rv);
        plrl.setOnRefreshListener(new PullLeftToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(PullLeftToRefreshActivity.this, "刷新数据成功", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public class PullAdapter extends RecyclerView.Adapter<PullAdapter.PullrHolder> {

        @Override
        public PullrHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pull_left, parent, false);
            return new PullrHolder(view);
        }

        @Override
        public void onBindViewHolder(PullrHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 10;
        }

        public class PullrHolder extends RecyclerView.ViewHolder {

            public PullrHolder(View itemView) {
                super(itemView);
            }
        }
    }
}
