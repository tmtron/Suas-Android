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
import zendesk.suas.Listener;
import zendesk.suas.Store;

public class MainActivity extends AppCompatActivity implements Listener {

    private Store store;
    private TodoListAdapter todoListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final RecyclerView todoList = findViewById(R.id.list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        todoList.setLayoutManager(layoutManager);

        todoListAdapter = new TodoListAdapter(new ArrayList<TodoItem>());
        todoList.setAdapter(todoListAdapter);

        store = ((TodoApplication) getApplication()).getStore();

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(
                        ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

                    @Override
                    public boolean isLongPressDragEnabled() {
                        return true;
                    }

                    public boolean onMove(
                            RecyclerView recyclerView,
                            ViewHolder viewHolder,
                            ViewHolder target) {

                        final int fromPos = viewHolder.getAdapterPosition();
                        final int toPos = target.getAdapterPosition();

                        Pair<Integer, Integer> fromToPositions = new Pair<>(fromPos, toPos);
                        Action moveAction = TodoActionFactory.moveAction(fromToPositions);
                        store.dispatch(moveAction);

                        return true;
                    }

                    @Override
                    public boolean isItemViewSwipeEnabled() {
                        return true;
                    }

                    public void onSwiped(ViewHolder viewHolder, int direction) {
                        Action deleteAction =
                                TodoActionFactory.deleteAction(viewHolder.getAdapterPosition());
                        store.dispatch(deleteAction);
                    }
                });

        itemTouchHelper.attachToRecyclerView(todoList);

        final EditText newItemInput = findViewById(R.id.new_item_input);
        findViewById(R.id.add_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newTitle = newItemInput.getText().toString();
                Action addAction = TodoActionFactory.addAction(newTitle);
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
    public void update(@NonNull Object state) {
        if (state instanceof TodoList) {
            TodoList todoList = (TodoList) state;
            todoListAdapter.update(todoList.getItems());
            return;
        }

        if (state instanceof TodoSettings) {
            TodoSettings todoSettings = (TodoSettings) state;
            todoListAdapter.setTheme(
                    todoSettings.getBackgroundColor(),
                    todoSettings.getTextColor());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        store.addListener(TodoSettings.class, this).informWithCurrentState();
        store.addListener(TodoList.class, this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        store.removeListener(this);
    }

    class TodoListAdapter extends RecyclerView.Adapter<TodoListAdapter.ViewHolder> {

        private final List<TodoItem> items;
        private int backgroundColor;
        private int textColor;

        class ViewHolder extends RecyclerView.ViewHolder {

            private final View container;
            private final TextView titleLabel;
            private final CheckBox checkBox;

            ViewHolder(View view) {
                super(view);

                container = view;
                titleLabel = view.findViewById(R.id.label);
                checkBox = view.findViewById(R.id.checkbox);

                view.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Action toggleAction = TodoActionFactory.toggleAction(getAdapterPosition());
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

            void setBackgroundColor(int color) {
                container.setBackgroundColor(color);
            }

            void setTextColor(int color) {
                titleLabel.setTextColor(color);
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

        void setTheme(int backgroundColor, int textColor) {
            this.backgroundColor = backgroundColor;
            this.textColor = textColor;
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
            holder.setBackgroundColor(backgroundColor);
            holder.setTextColor(textColor);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }
}
