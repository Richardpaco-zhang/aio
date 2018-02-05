package rd.zhang.aio.kotlin.core.ext.process;

import android.os.AsyncTask;

/**
 * Created by Richard on 2017/10/20.
 */
public abstract class ResultTask<R> {

    public abstract R process();

    public void execute(final ResultCallback<R> taskResult) {
        new AsyncTask<Void, Void, R>() {

            @Override
            protected R doInBackground(Void... voids) {
                return process();
            }

            @Override
            protected void onPostExecute(R r) {
                super.onPostExecute(r);
                if (taskResult != null) {
                    taskResult.onResult(r);
                }
            }

        }.execute();
    }
}
