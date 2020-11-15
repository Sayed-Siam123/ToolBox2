package com.rapples.arafat.toolbox2;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.uk.tsl.rfid.DeviceListActivity;
import com.uk.tsl.rfid.asciiprotocol.AsciiCommander;
import com.uk.tsl.rfid.asciiprotocol.commands.BatteryStatusCommand;
import com.uk.tsl.rfid.asciiprotocol.device.ConnectionState;
import com.uk.tsl.rfid.asciiprotocol.device.IAsciiTransport;
import com.uk.tsl.rfid.asciiprotocol.device.ObservableReaderList;
import com.uk.tsl.rfid.asciiprotocol.device.Reader;
import com.uk.tsl.rfid.asciiprotocol.device.ReaderManager;
import com.uk.tsl.rfid.asciiprotocol.device.TransportType;
import com.uk.tsl.rfid.asciiprotocol.responders.IAsciiCommandResponder;
import com.uk.tsl.rfid.asciiprotocol.responders.LoggerResponder;
import com.uk.tsl.utils.Observable;

import java.util.ArrayList;
import java.util.List;

import static com.uk.tsl.rfid.DeviceListActivity.EXTRA_DEVICE_ACTION;
import static com.uk.tsl.rfid.DeviceListActivity.EXTRA_DEVICE_INDEX;







/**
 * Activity demonstrating the ReaderManager class and how to auto detect ePop-Loq Readers
 * and use the DeviceListActivity to select BT Readers
 */
public class ReaderManagerActivity extends AppCompatActivity
{
    private RecyclerView mRecyclerView;
    private MessageViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    List<String> mMessages = new ArrayList<>();

    // The Reader currently in use
    private Reader mReader = null;
    private boolean mIsSelectingReader = false;

    private MenuItem mConnectMenuItem;
    private MenuItem mDisconnectMenuItem;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader_manager);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Snackbar.make(view, "Message list", Snackbar.LENGTH_LONG)
                        .setAction("Clear", new View.OnClickListener() {
                            @Override
                            public void onClick(View v)
                            {
                                if( mAdapter != null )
                                {
                                    mMessages.clear();
                                    mAdapter.notifyDataSetChanged();
                                }
                            }
                        }).show();
            }
        });

        // Configure the message list
        mRecyclerView = findViewById(R.id.message_recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MessageViewAdapter(mMessages);
        mRecyclerView.setAdapter( mAdapter );

        // Ensure the shared instance of AsciiCommander exists
        AsciiCommander.createSharedInstance(getApplicationContext());

        final AsciiCommander commander = getCommander();

        // Ensure that all existing responders are removed
        commander.clearResponders();

        // Add the LoggerResponder - this simply echoes all lines received from the reader to the log
        // and passes the line onto the next responder
        // This is ADDED FIRST so that no other responder can consume received lines before they are logged.
        commander.addResponder(new LoggerResponder());

        //
        // Add a simple Responder that sends the Reader output to the App message list
        //
        // Note - This is not the recommended way of receiving Reader input - it is just a convenient
        // way to show that the Reader is connected and communicating - see the other Sample Projects
        // for how to Inventory, Read, Write etc....
        //
        commander.addResponder(new IAsciiCommandResponder() {
            @Override
            public boolean isResponseFinished() { return false; }

            @Override
            public void clearLastResponse() {}

            @Override
            public boolean processReceivedLine(String fullLine, boolean moreLinesAvailable)
            {
                appendMessage("> " + fullLine);
                // don't consume the line - allow others to receive it
                return false;
            }
        });

        // Add responder to enable the synchronous commands
        commander.addSynchronousResponder();

        // Configure the ReaderManager when necessary
        ReaderManager.create(getApplicationContext());

        // Add observers for changes
        ReaderManager.sharedInstance().getReaderList().readerAddedEvent().addObserver(mAddedObserver);
        ReaderManager.sharedInstance().getReaderList().readerUpdatedEvent().addObserver(mUpdatedObserver);
        ReaderManager.sharedInstance().getReaderList().readerRemovedEvent().addObserver(mRemovedObserver);


    }


    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        // Remove observers for changes
        ReaderManager.sharedInstance().getReaderList().readerAddedEvent().removeObserver(mAddedObserver);
        ReaderManager.sharedInstance().getReaderList().readerUpdatedEvent().removeObserver(mUpdatedObserver);
        ReaderManager.sharedInstance().getReaderList().readerRemovedEvent().removeObserver(mRemovedObserver);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reader_manager, menu);

        mConnectMenuItem = menu.findItem(R.id.connect_reader_menu_item);
        mDisconnectMenuItem= menu.findItem(R.id.disconnect_reader_menu_item);

        return true;
    }


    /**
     * Prepare the menu options
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        boolean isConnected = getCommander().isConnected();
        mDisconnectMenuItem.setEnabled(isConnected);

        mConnectMenuItem.setEnabled(true);
        mConnectMenuItem.setTitle( (mReader != null && mReader.isConnected() ? R.string.change_reader_menu_item_text : R.string.connect_reader_menu_item_text));

        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {

            case R.id.action_settings:
                // Handle settings here...
                return true;

            case R.id.connect_reader_menu_item:
                // Launch the DeviceListActivity to see available Readers
                mIsSelectingReader = true;
                int index = -1;
                if( mReader != null )
                {
                    index = ReaderManager.sharedInstance().getReaderList().list().indexOf(mReader);
                }
                Intent selectIntent = new Intent(this, DeviceListActivity.class);
                if( index >= 0 )
                {
                    selectIntent.putExtra(EXTRA_DEVICE_INDEX, index);
                }
                startActivityForResult(selectIntent, DeviceListActivity.SELECT_DEVICE_REQUEST);
                return true;

            case R.id.disconnect_reader_menu_item:
                if( mReader != null )
                {
                    mReader.disconnect();
                    // Explicitly clear the Reader as we are finished with it
                    mReader = null;
                }

                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    // Append the given message to the bottom of the message area
    private void appendMessage(String message)
    {
        final String msg = message;
        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                mMessages.add(msg);
                final int modifiedIndex = mMessages.size() - 1;
                mAdapter.notifyItemInserted(modifiedIndex);
                mRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run()
                    {
                        int listEndIndex = mMessages.size() - 1;
                        mRecyclerView.smoothScrollToPosition(listEndIndex);
                    }
                }, 20);
            }
        });
    }


    @Override
    protected void onResume()
    {
        super.onResume();

        appendMessage("Resuming...");

        // Register to receive notifications from the AsciiCommander
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(AsciiCommander.STATE_CHANGED_NOTIFICATION));

        // Remember if the pause/resume was caused by ReaderManager - this will be cleared when ReaderManager.onResume() is called
        boolean readerManagerDidCauseOnPause = ReaderManager.sharedInstance().didCauseOnPause();

        // The ReaderManager needs to know about Activity lifecycle changes
        ReaderManager.sharedInstance().onResume();

        // The Activity may start with a reader already connected (perhaps by another App)
        // Update the ReaderList which will add any unknown reader, firing events appropriately
        ReaderManager.sharedInstance().updateList();

        // Reconnect to the Reader in use (locate a Reader to use when necessary)
        AutoSelectReader(!readerManagerDidCauseOnPause);

        mIsSelectingReader = false;
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        appendMessage("Pausing...");

        // Register to receive notifications from the AsciiCommander
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);

        // Disconnect from the reader to allow other Apps to use it
        // unless pausing when USB device attached or using the DeviceListActivity to select a Reader
        if( !mIsSelectingReader && !ReaderManager.sharedInstance().didCauseOnPause() && mReader != null )
        {
            mReader.disconnect();
        }

        ReaderManager.sharedInstance().onPause();
    }


    //
    // Select the Reader to use and reconnect to it as needed
    //
    private void AutoSelectReader(boolean attemptReconnect)
    {
        ObservableReaderList readerList = ReaderManager.sharedInstance().getReaderList();
        Reader usbReader = null;
        if( readerList.list().size() >= 1)
        {
            // Currently only support a single USB connected device so we can safely take the
            // first CONNECTED reader if there is one
            for (Reader reader : readerList.list())
            {
                if (reader.hasTransportOfType(TransportType.USB))
                {
                    usbReader = reader;
                    break;
                }
            }
        }

        if( mReader == null )
        {
            if( usbReader != null )
            {
                // Use the Reader found, if any
                mReader = usbReader;
                getCommander().setReader(mReader);
            }
        }
        else
        {
            // If already connected to a Reader by anything other than USB then
            // switch to the USB Reader
            IAsciiTransport activeTransport = mReader.getActiveTransport();
            if ( activeTransport != null && activeTransport.type() != TransportType.USB && usbReader != null)
            {
                appendMessage("Disconnecting from: " + mReader.getDisplayName() + "\n");
                mReader.disconnect();

                mReader = usbReader;

                // Use the Reader found, if any
                getCommander().setReader(mReader);
            }
        }

        // Reconnect to the chosen Reader
        if( mReader != null
                && !mReader.isConnecting()
                && (mReader.getActiveTransport()== null || mReader.getActiveTransport().connectionStatus().value() == ConnectionState.DISCONNECTED))
        {
            // Attempt to reconnect on the last used transport unless the ReaderManager is cause of OnPause (USB device connecting)
            if( attemptReconnect )
            {
                if( mReader.allowMultipleTransports() || mReader.getLastTransportType() == null )
                {
                    // Reader allows multiple transports or has not yet been connected so connect to it over any available transport
                    if( mReader.connect() )
                    {
                        appendMessage("Connecting to: " + mReader.getDisplayName() +"\n");
                    }
                }
                else
                {
                    // Reader supports only a single active transport so connect to it over the transport that was last in use
                    if( mReader.connect(mReader.getLastTransportType()) )
                    {
                        appendMessage("Connecting (over last transport) to: " + mReader.getDisplayName() +"\n");
                    }
                }
            }
        }
    }


    // ReaderList Observers
    Observable.Observer<Reader> mAddedObserver = new Observable.Observer<Reader>()
    {
        @Override
        public void update(Observable<? extends Reader> observable, Reader reader)
        {
            // See if this newly added Reader should be used
            AutoSelectReader(true);
        }
    };

    Observable.Observer<Reader> mUpdatedObserver = new Observable.Observer<Reader>()
    {
        @Override
        public void update(Observable<? extends Reader> observable, Reader reader)
        {
        }
    };

    Observable.Observer<Reader> mRemovedObserver = new Observable.Observer<Reader>()
    {
        @Override
        public void update(Observable<? extends Reader> observable, Reader reader)
        {
            // Was the current Reader removed
            if( reader == mReader)
            {
                mReader = null;

                // Stop using the old Reader
                getCommander().setReader(mReader);
            }
        }
    };


    /**
     * @return the current AsciiCommander
     */
    protected AsciiCommander getCommander()
    {
        return AsciiCommander.sharedInstance();
    }


    //
    // Handle the messages broadcast from the AsciiCommander
    //
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String connectionStateMsg = getCommander().getConnectionState().toString();
            Log.d("", "AsciiCommander state changed - isConnected: " + getCommander().isConnected() + " (" + connectionStateMsg + ")");

            if(getCommander()!= null)
            {
                if (getCommander().isConnected())
                {
                    // Report the battery level when Reader connects
                    BatteryStatusCommand bCommand = BatteryStatusCommand.synchronousCommand();
                    getCommander().executeCommand(bCommand);
                    int batteryLevel = bCommand.getBatteryLevel();
                    appendMessage(String.format("Reader: %s (%d%%)", mReader == null ? "???" : mReader.getDisplayName(), batteryLevel));
                }
                else if(getCommander().getConnectionState() == ConnectionState.DISCONNECTED)
                {
                    // A manual disconnect will have cleared mReader
                    if( mReader != null )
                    {
                        appendMessage(String.format("Reader: %s disconnected", mReader.getDisplayName()));
                        // See if this is from a failed connection attempt
                        if (!mReader.wasLastConnectSuccessful())
                        {
                            appendMessage(String.format("Failed to connect!"));
                            // Unable to connect so have to choose reader again
                            mReader = null;
                        }
                    }
                }
            }

        }
    };


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case DeviceListActivity.SELECT_DEVICE_REQUEST:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    int readerIndex = data.getExtras().getInt(EXTRA_DEVICE_INDEX);
                    Reader chosenReader = ReaderManager.sharedInstance().getReaderList().list().get(readerIndex);

                    int action = data.getExtras().getInt(EXTRA_DEVICE_ACTION);

                    // If already connected to a different reader then disconnect it
                    if (mReader != null) {
                        if (action == DeviceListActivity.DEVICE_CHANGE || action == DeviceListActivity.DEVICE_DISCONNECT) {
                            appendMessage("Disconnecting from: " + mReader.getDisplayName() + "\n");
                            mReader.disconnect();
                            if (action == DeviceListActivity.DEVICE_DISCONNECT) {
                                mReader = null;
                            }
                        }
                    }

                    // Use the Reader found
                    if (action == DeviceListActivity.DEVICE_CHANGE || action == DeviceListActivity.DEVICE_CONNECT) {
                        mReader = chosenReader;
                        appendMessage("Selected: " + mReader.getDisplayName());
                        getCommander().setReader(mReader);
                    }
                }
                break;
        }
    }

}
