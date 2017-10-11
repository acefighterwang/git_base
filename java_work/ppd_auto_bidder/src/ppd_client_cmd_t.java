/***
 *   class ppd_client_cmd_t is just the caller of 
 *   our [ppd_auto_bidder] module.
 *   It only work in command line environment on Windows/Linux.
 *   Also there would be other clients just like ppd_client_dialog or ppd_client_web
 *   which would work in a UI dialog or web page.
 *   
 *    
 * @author Adam Wang
 * @date   2017-Oct-8th
 */


import java.util.Date;
//import java.lang.reflect.Method;




public class ppd_client_cmd_t implements ppd_client_sink_it
{
	public static ppd_client_it PPD_CLT = null;
	
	
	public static void main(String[] args)
	{
		//the following code is for test only
		create_ppd_client();
		if(PPD_CLT!=null)
		{
			PPD_CLT.command_request(ppd_client_it.CMD_TYPE_LOAD_BID_ACCOUNT);
			PPD_CLT.command_request(ppd_client_it.CMD_TYPE_START_ALL_BIDDERS);
			PPD_CLT.command_request(ppd_client_it.CMD_TYPE_STOP_ALL_BIDDERS);
		}
		
		
		while(true)
		{
			try{
				Thread.sleep(1000);				
			}catch(InterruptedException ie)
			{
				
			}
			
			
		}
	}
	
	
	public static ppd_client_it create_ppd_client()
	{
		if(PPD_CLT!=null)return PPD_CLT;
		PPD_CLT = new ppd_bid_manager_t();
		return PPD_CLT;
	}
/*------------------------------------> for interface [ppd_client_sink_it] <------------------------------------*/
	public void on_status_update_indication()
	{
		
	}
	
	public void on_running_log_indication()
	{
		
	}
	
	public void on_command_responed(int command_type)
	{
		
	}
}