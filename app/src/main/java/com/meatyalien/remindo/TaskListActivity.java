package com.meatyalien.remindo;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.location.Location;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import android.widget.Button;

import android.widget.CompoundButton;
import android.widget.LinearLayout;

import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Switch;
import android.widget.Toast;

import com.meatyalien.remindo.models.Task;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import static android.view.View.VISIBLE;

public class TaskListActivity extends AppCompatActivity {

    private Realm realm;
    private View addTaskView;
    private View editTaskView;
    private View profileView;

    Button btnGetLoc;
    private int mInterval = 5000; // 5 seconds by default, can be changed later
    private Handler mHandler;
    private static final int CHANNEL = 1200;
    private NotificationHelper noti;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        mHandler = new Handler();
        startRepeatingTask();

        realm = Realm.getDefaultInstance();

        RealmResults<Task> tasks = realm.where(Task.class).findAll();
        final TaskAdapter adapter = new TaskAdapter(this, tasks);

        noti = new NotificationHelper(this);


        ListView listView = (ListView) findViewById(R.id.task_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Task task = (Task) adapterView.getAdapter().getItem(i);
                //final EditText taskEditText = new EditText(TaskListActivity.this);

                LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                editTaskView = inflater.inflate(R.layout.edit_task, null);

                //Views for Task Name
                EditText tasknameview = (EditText)editTaskView.findViewById(R.id.taskname);
                tasknameview.setText(task.getName());
                final EditText tasknameview_final = tasknameview;

                //Views for Task Date
                EditText taskdateview = (EditText)editTaskView.findViewById(R.id.taskdate);
                taskdateview.setText(Long.toString(task.getTimestamp()));
                final EditText taskdateview_final = taskdateview;

                //Views for Lat/Lon
                EditText tasklatview = (EditText)editTaskView.findViewById(R.id.xgps);
                tasklatview.setText(Double.toString(task.getXgps()));
                final EditText tasklatview_final = tasklatview;
                EditText tasklonview = (EditText)editTaskView.findViewById(R.id.ygps);
                tasklonview.setText(Double.toString(task.getYgps()));
                final EditText tasklonview_final = tasklonview;

                Switch reminder = (Switch) editTaskView.findViewById(R.id.reminder_switch);
                reminder.setChecked(task.getReminder());
                final Switch reminder_switch = reminder;

                final EditText tdate = editTaskView.findViewById(R.id.taskdate);
                final LinearLayout tlinearLayout2 = editTaskView.findViewById(R.id.linearLayout2);
                if (task.getType()){
                    tdate.setVisibility(View.INVISIBLE);
                    tlinearLayout2.setVisibility(VISIBLE);
                }
                else{
                    tdate.setVisibility(VISIBLE);
                    tlinearLayout2.setVisibility(View.INVISIBLE);
                }

                //taskEditText.setText(task.getName());
                AlertDialog dialog = new AlertDialog.Builder(TaskListActivity.this)
                        .setTitle("Edit Task")
                        .setView(editTaskView)
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                changeTaskName(task.getId(),
                                        String.valueOf(tasknameview_final.getText()),
                                        String.valueOf(taskdateview_final.getText()),
                                        String.valueOf(tasklatview_final.getText()),
                                        String.valueOf(tasklonview_final.getText()),
                                        reminder_switch.isChecked());
                            }
                        })
                        .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteTask(task.getId());
                            }
                        })
                        .create();
                dialog.show();
            }
        });


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //final EditText taskEditText = new EditText(TaskListActivity.this);
                LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                addTaskView = inflater.inflate(R.layout.add_task, null);

                //set default time
                final EditText taskdateview = (EditText)addTaskView.findViewById(R.id.taskdate);
                taskdateview.setText(Long.toString(System.currentTimeMillis()));

                //set placeholder lat/lon
                final EditText xgpsview = (EditText)addTaskView.findViewById(R.id.xgps);
                xgpsview.setText("900");
                final EditText ygpsview = (EditText)addTaskView.findViewById(R.id.ygps);
                ygpsview.setText("900");
                final Switch type =  addTaskView.findViewById(R.id.type);
                final EditText taskdate = addTaskView.findViewById(R.id.taskdate);
                final LinearLayout linearLayout2 = addTaskView.findViewById(R.id.linearLayout2);
                type.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked){
                            taskdate.setVisibility(View.INVISIBLE);
                            linearLayout2.setVisibility(VISIBLE);
                            type.setText("GPS");
                            taskdateview.setText(Long.toString((long) 0));
                        }
                        else{
                            taskdate.setVisibility(VISIBLE);
                            linearLayout2.setVisibility(View.INVISIBLE);
                            taskdateview.setText(Long.toString(System.currentTimeMillis()));
                            xgpsview.setText("900");
                            xgpsview.setText("900");
                            type.setText("Time");
                        }
                    }
                });
                AlertDialog dialog = new AlertDialog.Builder(TaskListActivity.this)
                        .setTitle("Add Task")
                        .setView(addTaskView)
                        //.setMessage("Name:")
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                realm.executeTransactionAsync(new Realm.Transaction() {
                                    EditText t_name = (EditText)addTaskView.findViewById(R.id.taskname);
                                    EditText t_date = (EditText)addTaskView.findViewById(R.id.taskdate);
                                    EditText t_xgps = (EditText)addTaskView.findViewById(R.id.xgps);
                                    EditText t_ygps = (EditText)addTaskView.findViewById(R.id.ygps);
                                    Switch aSwitch = (Switch) addTaskView.findViewById(R.id.type);
                                    Switch reminderSwitch = (Switch) addTaskView.findViewById(R.id.reminder_switch);

                                    @Override
                                    public void execute(Realm realm) {
                                        Task task = realm.createObject(Task.class, UUID.randomUUID().toString());
                                        task.setName(t_name.getText().toString());
                                        task.setTimestamp(Long.parseLong(t_date.getText().toString()));
                                        task.setXgps(Double.parseDouble(t_xgps.getText().toString()));
                                        task.setYgps(Double.parseDouble(t_ygps.getText().toString()));
                                        task.setType(aSwitch.isChecked());
                                        task.setReminder(reminderSwitch.isChecked());
                                        //realm.createObject(Task.class, UUID.randomUUID().toString()).setTimestamp(t_date.getText().toString());
                                    }
                                });
                            }

                        }
                        )
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();

            }
        });

    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                GPSTracker g = new GPSTracker(getApplicationContext());
                Location l = g.getLocation();
                if(l != null){
                    double lat = l.getLatitude();
                    double lon = l.getLongitude();
                    Date currentTime = Calendar.getInstance().getTime();
                    try{
                        RealmResults<Task> result = realm.where(Task.class)
                                .between("xgps", lon - .5,lon + .5)
                                .and()
                                .between("ygps", lat-.5,lat+.5)
                                .and()
                                .equalTo("done",false)
                                .and()
                                .equalTo("reminder",true)
                                .findAll();
                        if(!result.isEmpty()) {
                            for (Task t : result) {
                                realm.beginTransaction();
                                sendNotification(CHANNEL, t.toString());
                                t.setDone(true);
                                realm.commitTransaction();
                            }
                        }
                        RealmResults<Task> result2 = realm.where(Task.class)
                                .between("timestamp", currentTime.getTime() - 30000, currentTime.getTime() + 30000)
                                .and()
                                .equalTo("done",false)
                                .and()
                                .equalTo("reminder",true)
                                .findAll();
                        if(!result2.isEmpty()){
                            for(Task t : result2){
                                realm.beginTransaction();
                                sendNotification(CHANNEL, t.toString());
                                t.setDone(true);
                                realm.commitTransaction();
                            }
                        }
                    }catch (Exception e){
                        Log.d("myTag", e.getMessage());
                    }
                }
            }finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                mHandler.postDelayed(mStatusChecker, mInterval);
            }
        }
    };

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    public void changeTaskDone(final String taskId) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Task task = realm.where(Task.class).equalTo("id", taskId).findFirst();
                task.setDone(!task.isDone());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_task_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_profile) {

            //final EditText taskEditText = new EditText(TaskListActivity.this);
            LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
            profileView = inflater.inflate(R.layout.profile, null);


            AlertDialog dialog = new AlertDialog.Builder(TaskListActivity.this)
                    .setTitle("Profile")
                    .setView(profileView)
                    //.setMessage("Name:")
                    .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .create();
            dialog.show();



            return true;
        }
        else if (id == R.id.action_delete) {
            deleteAllDone();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void changeTaskName(final String taskId, final String name, final String date, final String xgps, final String ygps, final boolean reminder) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Task task = realm.where(Task.class).equalTo("id", taskId).findFirst();
                task.setName(name);
                task.setTimestamp(Long.parseLong(date));
                task.setXgps(Double.parseDouble(xgps));
                task.setYgps(Double.parseDouble(ygps));
                task.setReminder(reminder);
            }
        });
    }

    private void deleteTask(final String taskId) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(Task.class).equalTo("id", taskId)
                        .findFirst()
                        .deleteFromRealm();
            }
        });
    }

    private void deleteAllDone() {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(Task.class).equalTo("done", true)
                        .findAll()
                        .deleteAllFromRealm();
            }
        });
    }

    public void sendNotification(int id, String title) {
        Notification.Builder nb = noti.getNotification(title, getString(R.string.secondary1_body));
        noti.notify(id, nb);
    }

}
