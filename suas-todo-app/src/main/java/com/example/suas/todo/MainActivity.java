package com.example.suas.todo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
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

        final RecyclerView todoList = findViewById(R.id.list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        todoList.setLayoutManager(layoutManager);

        final TodoListAdapter todoListAdapter = new TodoListAdapter(new ArrayList<TodoItem>());
        todoList.setAdapter(todoListAdapter);

        final Middleware middleware = new LoggerMiddleware.Builder()
                .withSerialization(LoggerMiddleware.Serialization.TO_STRING)
                .withLineLength(120)
                .build();

        store = Suas.createStore(new TodoReducer())
                .withMiddleware(middleware)
                .withDefaultFilter(Filters.EQUALS)
                .build();

        store.addListener(TodoList.class, new Listener<TodoList>() {
            @Override
            public void update(@NonNull TodoList e) {
                todoListAdapter.update(e.getItems());
            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(
                        ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

                    @Override
                    public boolean isLongPressDragEnabled() {
                        return true;
                    }

                    public boolean onMove(RecyclerView recyclerView,
                            ViewHolder viewHolder, ViewHolder target) {

                        final int fromPos = viewHolder.getAdapterPosition();
                        final int toPos = target.getAdapterPosition();

                        Pair<Integer, Integer> fromToPositions = new Pair<>(fromPos, toPos);
                        Action moveAction = ActionFactory.moveAction(fromToPositions);
                        store.dispatch(moveAction);

                        return true;
                    }

                    @Override
                    public boolean isItemViewSwipeEnabled() {
                        return true;
                    }

                    public void onSwiped(ViewHolder viewHolder, int direction) {
                        Action deleteAction = ActionFactory
                                .deleteAction(viewHolder.getAdapterPosition());
                        store.dispatch(deleteAction);
                    }
                });

        itemTouchHelper.attachToRecyclerView(todoList);

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

    class TodoListAdapter extends RecyclerView.Adapter<TodoListAdapter.ViewHolder> {

        final List<TodoItem> items;

        class ViewHolder extends RecyclerView.ViewHolder {

            private final TextView titleLabel;
            private final CheckBox checkBox;

            ViewHolder(View view) {
                super(view);

                titleLabel = view.findViewById(R.id.label);
                checkBox = view.findViewById(R.id.checkbox);

                view.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Action toggleAction = ActionFactory.toggleAction(getAdapterPosition());
                        store.dispatch(toggleAction);
                    }
                });
            }

            void setTitle(String title) {
                titleLabel.setText(title);
            }

            void setIsCompleted(boolean isCompleted) {
                checkBox.setChecked(isCompleted);
            }
        }

        TodoListAdapter(List<TodoItem> items) {
            this.items = items;
        }

        void update(List<TodoItem> items) {
            this.items.clear();
            this.items.addAll(items);
            notifyDataSetChanged();
        }

        @Override
        public TodoListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                int viewType) {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item, parent, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            TodoItem todoItem = items.get(position);
            holder.setTitle(todoItem.getTitle());
            holder.setIsCompleted(todoItem.isCompleted());
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }
}
