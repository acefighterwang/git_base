/***
 *  trace_secretary_t is a SINGLETONE in our project which is working on
 *  the stuff about trace writing into files.
 *  This singletone instance works in its own thread to check whether 
 *  there are new traces need to push from buffer into files on disk.
 *  @author Adam Wang
 *  @date   2017-Oct-8th 
 */

import java.util.*;

class trace_secretary_t extends Thread
{
	public final static int TRACE_CHECK_INTERVAL = 100; //every 100ms we check whether there is trace need to write into file

	public static trace_secretary_t PL = new trace_secretary_t();
	public boolean m_is_stop;
	private boolean m_is_locked;
	public Vector<String> m_log_buffer;
	public byte[] m_trace_buffer_lock = new byte[0]; //we use this object as locker for log buffer

	
	
	
	
	trace_secretary_t()
	{
		m_is_stop = true;
		m_is_locked = false;		
	}
	
	public void set_log_file_path(String log_file_path, String log_file_name) //in main thread
	{
		
	}
	
	public void stop_trace_work()
	{
		m_is_stop = true;
	}
	
	public boolean is_locked()
	{
		return m_is_locked;
	}
	
	private void lock_it()
	{
		m_is_locked = true;
	}

	private void unlock_it()
	{
		m_is_locked = false;
	}
	
	
	public void check_trace_buffer()
	{
		if(this.is_locked())return;
		
		synchronized(this.m_trace_buffer_lock)
		{
			this.lock_it();
			if(this.m_log_buffer.size()==0)return;
			
			this.unlock_it();
			
		}		
	}
	
	public static void LOG_INFO(String str_info_log)
	{
		//get thread ID,
		//get date and time
		synchronized(trace_secretary_t.PL.m_trace_buffer_lock)
		{
			trace_secretary_t.PL.lock_it();
			
			trace_secretary_t.PL.unlock_it();
		}
	}
	
	public static void LOG_ERROR(String str_err_log)
	{
		//get thread ID,
		//get date and time
		synchronized(trace_secretary_t.PL.m_trace_buffer_lock)
		{
			trace_secretary_t.PL.lock_it();
			
			trace_secretary_t.PL.unlock_it();
		}
	}
	

	public static void LOG_DEBUG(String str_debug_log)
	{
		//get thread ID,
		//get date and time
		synchronized(trace_secretary_t.PL.m_trace_buffer_lock)
		{
			trace_secretary_t.PL.lock_it();
			
			trace_secretary_t.PL.unlock_it();
		}
	}
	

	public static void TERMINAL_OUTPUT(String consol_log)
	{
		//get thread ID,
		//get date and time
		System.out.println(consol_log);
	}
	
	public void run()
	{
		while(m_is_stop==false)
		{
			this.check_trace_buffer();
			try
			{
				sleep(TRACE_CHECK_INTERVAL);				
			}
			catch(InterruptedException ie) 
			{
				System.out.println("trace_secretary.run(), exception: " + ie);
			}			
		}		
	}
	
	
}