package com.example.suas.todo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import zendesk.suas.Action;
import zendesk.suas.Filters;
import zendesk.suas.Listener;
import zendesk.suas.LoggerMiddleware;
import zendesk.suas.Middleware;
import zendesk.suas.Store;
import zendesk.suas.Suas;

public class MainActivity extends AppCompatActivity implements Listener<TodoList> {

    private Store store;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ListView listView = findViewById(R.id.list);
        final TodoListAdapter todoListAdapter = new TodoListAdapter(new ArrayList<TodoItem>());
        listView.setAdapter(todoListAdapter);

        final Middleware middleware = new LoggerMiddleware.Builder()
                .withSerialization(LoggerMiddleware.Serialization.TO_STRING)
                .withLineLength(120)
                .build();

        store = Suas.createStore(new TodoReducer())
                .withMiddleware(middleware)
                .withDefaultFilter(Filters.EQUALS)
                .build();

        final EditText newItemInput = findViewById(R.id.new_item_input);
        findViewById(R.id.add_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newTitle = newItemInput.getText().toString();
                Action addAction = ActionFactory.addAction(newTitle);
                store.dispatch(addAction);

                newItemInput.setText("");
            }
        });

        findViewById(R.id.trigger_invalid_action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Action invalidAction = new Action("bla bla");
                store.dispatch(invalidAction);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Action deleteAction = ActionFactory.deleteAction(position);
                store.dispatch(deleteAction);
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

        final List<TodoItem> item;

        TodoListAdapter(List<TodoItem> item) {
            this.item = item;
        }

        void update(List<TodoItem> items) {
            item.clear();
            item.addAll(items);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return item.size();
        }

        @Override
        public TodoItem getItem(int i) {
            return item.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View v = view;
            if (v == null) {
                v = LayoutInflater
                        .from(viewGroup.getContext())
                        .inflate(android.R.layout.simple_list_item_1, viewGroup, false);
            }

            TextView txt = v.findViewById(android.R.id.text1);
            txt.setText(item.get(i).getTitle());

            return v;
        }
    }

}
