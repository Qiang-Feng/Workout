package com.qiang.workout.Utilities;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

public abstract class CountDown
{
	private static final int MSG = 1;
	private long mMillisInFuture;
	private long mTickInterval;
	private long mNextTime;
	private long mStopTimeInFuture;
	private long mPausedTime;
	private boolean mPaused = false;
	private boolean mStopped = false;
	private Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			synchronized (CountDown.this)
			{
				if (mPaused || mStopped)
				{
					return;
				}

				long mMillisLeft = mStopTimeInFuture - SystemClock.uptimeMillis();

				if (mMillisLeft <= 0)
				{
					onFinish();
				}
				else
				{
					onTick(mMillisLeft);

                    /*
                        Calculate next tick by adding the tick interval to original start time
                        If onTick() took too long, skip the intervals that were already missed
                    */
					long currentTime = SystemClock.uptimeMillis();

					do
					{
						mNextTime += mTickInterval;
					}
					while (currentTime > mNextTime);

					// Ensures this interval doesn't exceed stop time
					if (mNextTime < mStopTimeInFuture)
					{
						sendMessageAtTime(obtainMessage(MSG), mNextTime);
					}
					else
					{
						sendMessageAtTime(obtainMessage(MSG), mStopTimeInFuture);
					}
				}
			}
		}
	};

	public CountDown(long millisInFuture, long tickInterval)
	{
		this.mMillisInFuture = millisInFuture;
		this.mTickInterval = tickInterval;
	}

	public synchronized void start()
	{
		// Stops countdown if number of millis till countdown finish = 0
		if (mMillisInFuture <= 0)
		{
			onFinish();
			return;
		}

		mNextTime = SystemClock.uptimeMillis();
		mStopTimeInFuture = mNextTime + mMillisInFuture;

		mNextTime += mTickInterval;
		mHandler.sendMessageAtTime(mHandler.obtainMessage(MSG), mNextTime);
	}

	public synchronized void pause()
	{
		mPaused = true;
		mPausedTime = SystemClock.uptimeMillis();
	}

	public synchronized void resume()
	{
		mPaused = false;
		mStopTimeInFuture += SystemClock.uptimeMillis() - mPausedTime;
		mHandler.sendMessage(mHandler.obtainMessage(MSG));
	}

	public synchronized void stop()
	{
		mStopped = true;
		mHandler.removeMessages(MSG);
	}

	public abstract void onTick(long millisUntilFinished);

	public abstract void onFinish();
}