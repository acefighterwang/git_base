/***
 * this interface defined all the APIs a PPD client could be called
 * 
 * @author Adam Wang
 * @date   2017-Oct-8th
 *
 */





public interface ppd_client_it
{
	public final static int CMD_TYPE_BEGIN = 0;
	public final static int CMD_TYPE_LOAD_BID_ACCOUNT = 1;
	public final static int CMD_TYPE_START_ALL_BIDDERS = 2;
	
	public final static int CMD_TYPE_STOP_ALL_BIDDERS = 99;
	public final static int CMD_TYPE_END = 100;
	
	void command_request(int command_type);
}