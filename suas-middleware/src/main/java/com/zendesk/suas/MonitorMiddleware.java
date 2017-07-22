package com.zendesk.suas;

public class MonitorMiddleware implements Middleware {


    public MonitorMiddleware() {

    }

    @Override
    public void onAction(Action<?> action, GetState state, Dispatcher dispatcher, Continuation continuation) {

    }


/*
    object ReduxMonitor {

        val running = AtomicBoolean(false)

        fun init() {
            if(running.compareAndSet(false, true)) {
                val server = LocalSocketServer("main", "redux_monitor") {
                    handler(it)
                }
                Thread { server.run() }.start()
            }
        }

        val list = mutableListOf<String>()
        var lastString: String = "&&__&&__&&"

        fun handler(socket:LocalSocket) {
            try {
                val output = BufferedOutputStream(socket.outputStream)
                writeToStream(lastString, output)

                while(true) {
                    synchronized(this) {
                        if (list.isNotEmpty()) {
                            val str = list.removeAt(0) + "&&__&&__&&"
                            lastString = str
                            writeToStream(str, output)
                        }
                    }
                }

            } catch(e : Exception) {
                e.printStackTrace()
            }
        }

        fun writeToStream(data: String, outputStream:BufferedOutputStream) {
            outputStream.write(data.toByteArray(Charset.forName("ascii")))
            outputStream.flush()
        }

        fun sendStuff(actionType: String, actionData: Any?, newState: Any) {
            synchronized(this) {
                val json = Gson().toJson(StateUpdate(actionType, actionData, newState))
                list.add(json)
            }
        }

        data class StateUpdate(val action: String, val actionData: Any?, val state: Any)

    }
*/

    private static class StateUpdate {

        private final Action<?> action;
        private final State newState;

        private StateUpdate(Action<?> action, State newState) {
            this.action = action;
            this.newState = newState;
        }

        public Action<?> getAction() {
            return action;
        }

        public State getNewState() {
            return newState;
        }
    }

}
