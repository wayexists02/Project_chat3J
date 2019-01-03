package com.chat3j.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.chat3j.R;
import com.chat3j.core.NodeController;
import com.chat3j.core.client.Chat3JNode;
import com.chat3j.core.client.Communication;
import com.chat3j.core.options.Option;

public class MainActivity extends Activity {

    private PopupWindow mPopupWindow;
    private PopupWindow mMinimizeWindow;
    private Chat3JNode node;
    private NodeController nodeController;
    private ListViewAdapter adapter;
    private TextView tv_topic, tv_people;
    private Context mContext;
    private Activity mActivity;
    private LinearLayout mLinearLayout;
    private String[] items = {"VOICE MODE", "CHAT MODE"};
    private boolean check = false;
    private boolean update = false;
    private int which;
    private View customView;
    private View minimizeView;
    private NetworkingThread networkingThread;
    private boolean isShowing = false;
    private ListView chat_listView;
    private Thread updateThread;
    private AndroidTextArea currentTextArea;
    private EditText text_Send;
    private InputMethodManager inputMethodManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        mContext = getApplicationContext();
        mActivity = MainActivity.this;
        mLinearLayout = (LinearLayout) findViewById(R.id.linear_main);

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);

        customView = inflater.inflate(R.layout.popup_window, null);
        minimizeView = inflater.inflate(R.layout.minimize_window, null);

        new Thread() {
            @Override
            public void run() {
                node = new Chat3JNode("ABC");
                nodeController = new NodeController(node);
                nodeController.setMasterInformation("172.30.1.12", 10321, 10322);
                //nodeController.setMasterInformation("35.221.176.240",10321,10322);
                nodeController.open();
            }
        }.start();

        tv_topic = (TextView) customView.findViewById(R.id.textview_topic);
        tv_topic.setText("");
        tv_people = (TextView) customView.findViewById(R.id.textview_people);

        ListView listView = (ListView) customView.findViewById(R.id.listview);
        adapter = new ListViewAdapter();
        listView.setAdapter(adapter);

        text_Send = (EditText) customView.findViewById(R.id.text_Message);
        chat_listView = (ListView) customView.findViewById(R.id.listview_chat);
        inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        final Button btn_Popup = (Button) findViewById(R.id.button);
        final ImageButton btn_Create = (ImageButton) customView.findViewById(R.id.button_create);
        final ImageButton btn_Enter = (ImageButton) customView.findViewById(R.id.button_enter);
        final ImageButton btn_Leave = (ImageButton) customView.findViewById(R.id.button_leave);
        final ImageButton btn_Minimize = (ImageButton) customView.findViewById(R.id.button_minimize);
        final ImageButton btn_Close = (ImageButton) customView.findViewById(R.id.button_close);
        final ImageButton btn_Maximize = (ImageButton) minimizeView.findViewById(R.id.button_maximize);
        final ImageButton btn_Send = (ImageButton) customView.findViewById(R.id.button_send);

        btn_Popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isShowing) {
                            mPopupWindow.dismiss();
                            mMinimizeWindow.dismiss();
                        }
                        isShowing = true;
                        if (!MainActivity.this.isFinishing()) {
                            mPopupWindow = new PopupWindow(customView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                            mPopupWindow.showAtLocation(mLinearLayout, Gravity.CENTER, 0, 0);
                            mPopupWindow.setFocusable(true);
                            mPopupWindow.update();

                            update = true;
                            // 인원수를 업데이트 해주는 루프
                            final Thread updateThread = new Thread() {
                                @Override
                                public void run() {
                                    while (update) {
                                        if (!tv_topic.getText().equals("")) {
                                            tv_people.setText(String.valueOf(nodeController.getNumOfPeople(tv_topic.getText().toString()) + 1));
                                        }
                                        if (update)
                                            System.out.println("true");
                                        else
                                            System.out.println("false");
                                    }
                                }
                            };
                            updateThread.start();

                            btn_Create.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (!MainActivity.this.isFinishing()) {
                                                final EditText editText = new EditText(MainActivity.this);
                                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                                builder.setTitle("Type Topic");
                                                builder.setView(editText);
                                                builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int choice) {
                                                        which = choice;
                                                    }
                                                });
                                                builder.setPositiveButton("입력",
                                                        new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int choice) {
                                                                String topic = editText.getText().toString();
                                                                networkingThread = new NetworkingThread();
                                                                networkingThread.setTopic(topic);
                                                                networkingThread.setWhich(which);
                                                                networkingThread.setTYPE(NodeController.Task_TYPE.CREATE);
                                                                networkingThread.start();
                                                                dialog.dismiss();
                                                            }
                                                        });
                                                builder.setNegativeButton("취소",
                                                        new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                dialog.dismiss();
                                                            }
                                                        });
                                                builder.show();
                                            }
                                        }
                                    });
                                }
                            });

                            btn_Enter.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (!MainActivity.this.isFinishing()) {
                                                final EditText editText = new EditText(MainActivity.this);
                                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                                builder.setTitle("Type Topic");
                                                builder.setView(editText);
                                                builder.setPositiveButton("입력",
                                                        new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                String topic = editText.getText().toString();
                                                                networkingThread = new NetworkingThread();
                                                                networkingThread.setTopic(topic);
                                                                networkingThread.setTYPE(NodeController.Task_TYPE.ENTER);
                                                                networkingThread.start();
                                                                dialog.dismiss();
                                                            }
                                                        });
                                                builder.setNegativeButton("취소",
                                                        new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                dialog.dismiss();
                                                            }
                                                        });
                                                builder.show();
                                            }
                                        }
                                    });
                                }
                            });

                            text_Send.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                @Override
                                public void onFocusChange(View v, boolean hasFocus) {
                                    if (!hasFocus) {
                                        inputMethodManager.hideSoftInputFromWindow(text_Send.getWindowToken(), 0);
                                    } else {
                                        inputMethodManager.showSoftInput(text_Send, 0);
                                    }
                                }
                            });

                            btn_Leave.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String topic = tv_topic.getText().toString();
                                    networkingThread = new NetworkingThread();
                                    networkingThread.setTopic(topic);
                                    networkingThread.setTYPE(NodeController.Task_TYPE.LEAVE);
                                    networkingThread.start();
                                }
                            });

                            btn_Close.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    isShowing = false;
                                    new Thread() {
                                        @Override
                                        public void run() {
                                            nodeController.close();
                                        }
                                    }.start();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mPopupWindow.dismiss();
                                            update = false;
                                        }
                                    });
                                }
                            });

                            btn_Send.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            System.out.println("Send Text Message : " + text_Send.getText().toString());
                                            currentTextArea.inputQueue(text_Send.getText().toString());
                                            text_Send.setText("");
                                        }
                                    });
                                }
                            });

                            btn_Minimize.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mPopupWindow.dismiss();
                                            mMinimizeWindow = new PopupWindow(minimizeView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                            mMinimizeWindow.showAtLocation(mLinearLayout, Gravity.CENTER, 0, 0);
                                            update = false;
                                        }
                                    });
                                    btn_Maximize.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    mMinimizeWindow.dismiss();
                                                    mPopupWindow = new PopupWindow(customView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                                                    mPopupWindow.showAtLocation(mLinearLayout, Gravity.CENTER, 0, 0);
                                                    update = true;
                                                    final Thread updateThread = new Thread() {
                                                        @Override
                                                        public void run() {
                                                            while (update) {
                                                                if (!tv_topic.getText().equals("")) {
                                                                    tv_people.setText(String.valueOf(nodeController.getNumOfPeople(tv_topic.getText().toString())));
                                                                }
                                                                if (update)
                                                                    System.out.println("true");
                                                                else
                                                                    System.out.println("false");
                                                            }
                                                        }
                                                    };
                                                    updateThread.start();
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }
                    }
                });
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tv_topic.setText(((ListViewItem) adapter.getItem(position)).getTopic());
                tv_people.setText(String.valueOf(nodeController.getNumOfPeople(tv_topic.getText().toString())));
                if (nodeController.getCommunicationType(tv_topic.getText().toString()) == Communication.ECommunicationType.CHAT) {
                    chat_listView.setAdapter(((AndroidTextArea) nodeController.getAdapter(tv_topic.getText().toString())).getChatAdapter());
                    currentTextArea = (AndroidTextArea) nodeController.getAdapter(tv_topic.getText().toString());
                } else if (nodeController.getCommunicationType(tv_topic.getText().toString()) == Communication.ECommunicationType.VOICE)
                    chat_listView.setAdapter(null);
                text_Send.setText("");
            }
        });
    }

    private class NetworkingThread extends Thread {
        private NodeController.Task_TYPE TYPE;
        private int which;
        private String topic;

        @Override
        public void run() {
            if (TYPE == NodeController.Task_TYPE.CREATE) {
                System.out.println("which is " + which);
                Option<Boolean> opt = nodeController.createTopic(topic,
                        ((which == 0) ?
                                NodeController.CommunicationType.VOICE :
                                NodeController.CommunicationType.CHAT));
                if (opt.ok == true && opt.data == false) {
                    Toast.makeText(mContext, "FAILURE", Toast.LENGTH_LONG).show();
                } else {
                    NodeController.TaskObject taskObject = new NodeController.TaskObject(NodeController.Task_TYPE.CREATE, topic);
                    WaitTask waitTask = new WaitTask();
                    waitTask.execute(taskObject);
                }
            } else if (TYPE == NodeController.Task_TYPE.ENTER) {
                Option<Boolean> opt = nodeController.enterTopic(topic);
                NodeController.TaskObject taskObject = new NodeController.TaskObject(NodeController.Task_TYPE.ENTER, topic);
                WaitTask waitTask = new WaitTask();
                waitTask.execute(taskObject);
            } else if (TYPE == NodeController.Task_TYPE.LEAVE) {
                nodeController.exitFromTopic(topic);
                NodeController.TaskObject taskObject = new NodeController.TaskObject(NodeController.Task_TYPE.LEAVE, topic);
                WaitTask waitTask = new WaitTask();
                waitTask.execute(taskObject);
            }
        }

        public void setTYPE(NodeController.Task_TYPE TYPE) {
            this.TYPE = TYPE;
        }

        public void setTopic(String topic) {
            this.topic = topic;
        }

        public void setWhich(int which) {
            this.which = which;
        }
    }

    private class WaitTask extends AsyncTask<NodeController.TaskObject, Void, NodeController.TaskObject> {

        private Dialog dlg;

        @Override
        protected void onPreExecute() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dlg = new Dialog(MainActivity.this);
                    dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dlg.setContentView(R.layout.progress_window);
                    dlg.show();
                }
            });
        }

        @Override
        protected NodeController.TaskObject doInBackground(NodeController.TaskObject... taskObjects) {
            // String [0] : CREATE, ENTER, EXIT
            // String [1] : TOPIC
            check = nodeController.checkTopic(taskObjects[0]);
            return taskObjects[0];
        }

        @Override
        protected void onPostExecute(final NodeController.TaskObject taskObject) {
            System.out.println("onPostExecute");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("RUN");
                    if (check == true) {
                        System.out.println("SUCCESS");
                        Toast.makeText(mContext, "SUCCESS", Toast.LENGTH_LONG).show();
                        System.out.println("Print Toast");
                        if (taskObject.getTYPE() == NodeController.Task_TYPE.LEAVE) {
                            System.out.println("TYPE = LEAVE");
                            adapter.removeItem(taskObject.getTOPIC());
                            adapter.notifyDataSetChanged();
                            if (adapter.getCount() == 0) {
                                tv_topic.setText("");
                            } else {
                                tv_topic.setText(((ListViewItem) adapter.getItem(0)).getTopic());
                            }
                        } else {
                            System.out.println("TYPE = CREATE OR ENTER");
                            System.out.println(taskObject.getTOPIC() + " is TOPIC");
                            tv_topic.setText(taskObject.getTOPIC());
                            if (nodeController.getCommunicationType(taskObject.getTOPIC()) == Communication.ECommunicationType.CHAT) {
                                TextMessageAdapter textAdapter = new TextMessageAdapter();
                                AndroidTextArea textArea = new AndroidTextArea(text_Send, textAdapter, mActivity);
                                System.out.println("Set Text Area Adapter");
                                nodeController.setTextAreaAdapter(taskObject.getTOPIC(), textArea);
                                System.out.println("List View Set Adapter");
                                chat_listView.setAdapter(textAdapter);
                                currentTextArea = textArea;
                                adapter.addItem(taskObject.getTOPIC(), ListViewItem.MODE.CHAT);
                                text_Send.setText("");
                            } else if (nodeController.getCommunicationType(taskObject.getTOPIC()) == Communication.ECommunicationType.VOICE) {
                                adapter.addItem(taskObject.getTOPIC(), ListViewItem.MODE.VOICE);
                                text_Send.setText("");
                            }
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        System.out.println("FAILURE");
                        Toast.makeText(mContext, "FAILURE", Toast.LENGTH_LONG).show();
                    }
                    dlg.dismiss();
                }
            });
        }
    }
}

