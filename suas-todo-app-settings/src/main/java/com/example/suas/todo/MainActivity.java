package com.example.suas.todo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import zendesk.suas.State;
import zendesk.suas.StateSelector;
import zendesk.suas.Store;

public class MainActivity extends AppCompatActivity implements StateSelector<TodoListViewModel>,
        Listener<TodoListViewModel> {

    private Store store;
    private TodoListAdapter todoListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final RecyclerView todoList = findViewById(R.id.list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        todoList.setLayoutManager(layoutManager);

        todoListAdapter = new TodoListAdapter();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        store.addListener(this, this).informWithCurrentState();
    }

    @Override
    protected void onStop() {
        super.onStop();
        store.removeListener(this);
    }

    @Nullable
    @Override
    public TodoListViewModel selectData(@NonNull State state) {
        // this is the state-selector
        // maybe not the best example, because it uses the complete state
        TodoList todoList = state.getState(TodoList.class);
        TodoSettings todoSettings = state.getState(TodoSettings.class);
        return new TodoListViewModel(todoList, todoSettings);
    }

    @Override
    public void update(@NonNull TodoListViewModel todoListViewModel) {
        todoListAdapter.update(todoListViewModel);
    }

    class TodoListAdapter extends RecyclerView.Adapter<TodoListAdapter.ViewHolder> {

        private final List<TodoItem> items;
        private TodoSettings settings;

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

        TodoListAdapter() {
            this.items = new ArrayList<>();
        }

        void update(TodoListViewModel viewModel) {
            this.items.clear();
            this.items.addAll(viewModel.getTodoList().getItems());
            this.settings = viewModel.getTodoSettings();
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
            holder.setBackgroundColor(settings.getBackgroundColor());
            holder.setTextColor(settings.getTextColor());
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }
}
