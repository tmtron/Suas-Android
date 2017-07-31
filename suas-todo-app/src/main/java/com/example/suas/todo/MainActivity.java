package com.example.suas.todo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import zendesk.suas.Action;
import zendesk.suas.Filters;
import zendesk.suas.Listener;
import zendesk.suas.LoggerMiddleware;
import zendesk.suas.Middleware;
import zendesk.suas.Store;
import zendesk.suas.Suas;

public class MainActivity extends AppCompatActivity implements Listener<TodoList> {

    Store store;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ListView listView = findViewById(R.id.list);
        final TodoListAdapter todoListAdapter = new TodoListAdapter(new ArrayList<String>());
        listView.setAdapter(todoListAdapter);

        final Middleware build = new LoggerMiddleware.Builder()
                .withSerialization(LoggerMiddleware.Serialization.TO_STRING)
                .withLineLength(120)
                .build();

        store = Suas.createStore(new TodoReducer())
                .withMiddleware(build)
                .withDefaultFilter(Filters.EQUALS)
                .build();

        findViewById(R.id.add_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                store.dispatchAction(ActionFactory.addAction(UUID.randomUUID().toString()));
            }
        });

        findViewById(R.id.nothing).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                store.dispatchAction(new Action("bla bla"));
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = todoListAdapter.getItem(i);
                store.dispatchAction(ActionFactory.deleteAction(item));
            }
        });

        store.addListener(TodoList.class, new Listener<TodoList>() {
            @Override
            public void update(@NonNull TodoList e) {
                todoListAdapter.update(e.getItems());
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        store.addListener(TodoList.class, this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        store.removeListener(this);
        System.out.println();
    }

    @Override
    public void update(@NonNull TodoList todoList) {
        System.out.println("update update " + todoList.getItems());
    }


    class TodoListAdapter extends BaseAdapter {

        final List<String> item;

        TodoListAdapter(List<String> item) {
            this.item = item;
        }

        void update(List<String> items) {
            item.clear();
            item.addAll(items);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return item.size();
        }

        @Override
        public String getItem(int i) {
            return item.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View v = view;
            if(v == null) {
                v = LayoutInflater
                        .from(viewGroup.getContext())
                        .inflate(android.R.layout.simple_list_item_1, viewGroup, false);
            }

            TextView txt = v.findViewById(android.R.id.text1);
            txt.setText(item.get(i));

            return v;
        }
    }

}
