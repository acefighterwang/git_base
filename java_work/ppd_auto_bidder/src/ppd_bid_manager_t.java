/***
 *   class ppd_bid_manager_t is working on the all the management of 
 *   bid actions. it manages bid_investigater and bid_agents.
 *   And communicate with ppd_client *   
 *    
 * @author Adam Wang
 * @date   2017-Oct-8th
 */


import java.util.*;


public class ppd_bid_manager_t implements ppd_client_it
{
	
	private Vector<bid_investigater_t> m_bidder_list = new Vector<bid_investigater_t>();
	
	
	ppd_bid_manager_t()
	{
		
	}
	
	public void load_bidder_account_info_from_file()
	{
		//to load all the bidder account information from file or DB		
	}
	
	public void start_bidders()
	{
		//to create and run the bid_investigater objects for each account we load from file before
		
		//the following code is just for test, not real code
		bid_investigater_t crt_bidder = new bid_investigater_t(this);
		if(this.m_bidder_list!=null)this.m_bidder_list.add(crt_bidder);
		crt_bidder.set_bidder_account_info("acefighter@126.com", "13067970826", "12", "pdu4233800142");
		crt_bidder.start_run();
	}

	
/*------------------------------------> for interface [ppd_client_it] <------------------------------------*/
	public void command_request(int command_type)
	{
		
		switch(command_type)
		{
		case ppd_client_it.CMD_TYPE_LOAD_BID_ACCOUNT:
		{
			this.load_bidder_account_info_from_file();
		}
		break;
		
		case ppd_client_it.CMD_TYPE_START_ALL_BIDDERS:
		{
			this.start_bidders();
		}
		break;
		
		default:
			break;
		
		}
		
	}
}