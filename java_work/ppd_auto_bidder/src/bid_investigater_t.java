/***
 * class bid_investigater_t is used to login PPD WEB service and 
 * get all the bid projects from PPD database in every certain time interval.
 * 
 * @author Adam Wang
 * @date   2017-Oct-9th
 */

import java.util.Date;
import com.ppdai.open.core.*;

public class bid_investigater_t extends Thread
{
	public final static int HEART_BEAT_INTERVAL = 500;//500ms, the timer driver
	public final static int BID_PROJECT_BID_INTERVAL = 1000;//every 1 second we check all the bid items we had get to see which should be bid
	public final static int BID_PROJECT_UPDATE_INTERVAL = 3*1000;//every 3 seconds we connect to PPD web svc to update the bid items
	
	public final static int INVESTIGATER_STATUS_IDEL = 0;
	public final static int INVESTIGATER_STATUS_LOGIN = 1; //had finished PPD API init() and succeeded
	public final static int INVESTIGATER_STATUS_RUNNING = 2; //to update bid projects from PPD web service in every heart beat
	public final static int INVESTIGATER_STATUS_CLOSING = 3; //prepare for stop and quit
	public final static int INVESTIGATER_STATUS_CLOSED = 4; //prepare for stop and quit
	
	
	
	public Object m_ppd_open_api_client = null;	
	public int m_running_status = 0;
	public boolean m_is_stop = true;
	public int m_instance_id;
	
	public ppd_bid_manager_t m_bid_mgr = null;
	private String m_ppd_key_public;
	private String m_ppd_key_private;
	private String m_ppd_dev_account_id;
	
	private String m_bidder_email_account;
	private String m_bidder_phone_number;
	private String m_bidder_user_role_type;
	private String m_bidder_user_account_code; //this code get from PPD WEB service when we register succeeded
	
	
	bid_investigater_t(ppd_bid_manager_t bid_mgr)
	{
		this.m_bid_mgr = bid_mgr;
		this.m_ppd_key_public = pdd_websvc_api_url_t.DEV_CLT_PUBLIC_KEY;
		this.m_ppd_key_private = pdd_websvc_api_url_t.DEV_CLT_PRIVATE_KEY;
		this.m_ppd_dev_account_id = pdd_websvc_api_url_t.DEV_APP_ID;
		m_is_stop = true;
		this.set_status(INVESTIGATER_STATUS_IDEL);		
	}
	
	void set_bidder_account_info(String bidder_email_account, String bidder_phone_number, String bidder_user_type, String account_code)
	{		
		m_bidder_email_account = bidder_email_account;
		m_bidder_phone_number = bidder_phone_number;
		m_bidder_user_role_type =  bidder_user_type;
		m_bidder_user_account_code = account_code; //if we had not register before, this value set as "".
		trace_secretary_t.LOG_INFO("bid_invtg.set_bidder_account_info(), start, email: " + m_bidder_email_account + ", phone: " + m_bidder_phone_number + ", role_type: " + m_bidder_user_role_type + ", accont_code: " + m_bidder_user_account_code);
	}
	
	public void start_run()
	{
		int test_a = 100;
		trace_secretary_t.LOG_INFO("bid_investigater_t.start_run(), " + test_a + "," + m_is_stop);
		m_is_stop = false;
		this.start();
	}
	
	public void stop_run()
	{
		m_is_stop = true;
		this.set_status(INVESTIGATER_STATUS_CLOSING);
	}
	
	public int get_status()
	{
		return m_running_status;
	}
	
	public void set_status(int new_status)
	{
		m_running_status = new_status;
	}
	
	public int register_ppd_bid_account()
	{
		try{			
			trace_secretary_t.LOG_INFO("bid_invtg.register_ppd_bid_account(), start, email: " + m_bidder_email_account + ", phone: " + m_bidder_phone_number + ", role_type: " + m_bidder_user_role_type);
			String str_url_operation = pdd_websvc_api_url_t.API_URL_BIDDER_ACCOUNT_REGISTER;//"http://gw.open.ppdai.com/auth/registerservice/register";
			PropertyObject[] property_obj_array = {new PropertyObject("Mobile", m_bidder_phone_number, ValueTypeEnum.String), 
            		new PropertyObject("Email", m_bidder_email_account, ValueTypeEnum.String), 
            		new PropertyObject("Role", m_bidder_user_role_type, ValueTypeEnum.String)};            
			Result result = OpenApiClient.send(str_url_operation, property_obj_array);
			trace_secretary_t.TERMINAL_OUTPUT("bid_invtg.register_ppd_bid_account(), register done! result: " + (result.isSucess()? result.getContext():result.getErrorMessage()));
			trace_secretary_t.LOG_INFO("bid_invtg.register_ppd_bid_account(), register done! result: " + (result.isSucess()? result.getContext():result.getErrorMessage()));
		}catch(Exception et)
		{
			trace_secretary_t.LOG_INFO("bid_invtg.register_ppd_bid_account(), FATAL! register on PPD FAILED! EXCEPTION: " + et);
			trace_secretary_t.TERMINAL_OUTPUT("bid_invtg.register_ppd_bid_account(), FATAL! register on PPD FAILED! EXCEPTION: " + et);
			return -1;
		}		
		return 0;
	}
	

	public int login_ppd_web_sevice() //to call init method of PPD API
	{
		trace_secretary_t.LOG_INFO("bid_invtg.login_ppd_web_sevice(), start, m_ppd_dev_account_id: " + m_ppd_dev_account_id);
		trace_secretary_t.LOG_INFO("bid_invtg.login_ppd_web_sevice(), public_key: " + m_ppd_key_public);
		trace_secretary_t.LOG_INFO("bid_invtg.login_ppd_web_sevice(), private_key: " + m_ppd_key_private);
		try{
			//1st, init
			OpenApiClient.Init(m_ppd_dev_account_id, RsaCryptoHelper.PKCSType.PKCS8, this.m_ppd_key_public, this.m_ppd_key_private);
			trace_secretary_t.TERMINAL_OUTPUT("bid_invtg.login_ppd_web_sevice(), initialized on PPD finished!");
						
			//2nd, login with bidder account
			AuthInfo authInfo = null;
            //��Ҫ��Ȩtoken
            //step 1 ��ת��AC��oauth2.0���ϵ�¼ https://ac.ppdai.com/oauth2/login?AppID=XXXXXXXXXXXXX&ReturnUrl=http://mysite.com/auth/gettoken
            //setp 2 ��¼�ɹ��� oauth2.0 ��ת��http://mysite.com/auth/gettoken?code=XXXXXXXXXXXXXXXXXXXXXXXXXX
            String code = this.m_ppd_dev_account_id;//"your_code";//ͨ���û���Ȩ��ȡ��code
            authInfo = OpenApiClient.authorize(code);
            trace_secretary_t.TERMINAL_OUTPUT("bid_invtg.login_ppd_web_sevice(), PPD authorize finished!");
            String accessToken = authInfo.getAccessToken();
            trace_secretary_t.TERMINAL_OUTPUT("bid_invtg.login_ppd_web_sevice(), PPD token is: " + accessToken);
            String str_url_operation = pdd_websvc_api_url_t.API_URL_BIDDER_ACCOUNT_AUTOLOGIN;// "http://gw.open.ppdai.com/auth/LoginService/AutoLogin";
            PropertyObject[] property_obj_array_2 = {new PropertyObject("Timestamp", new Date(), ValueTypeEnum.DateTime)};
            Result result = OpenApiClient.send(str_url_operation, accessToken, property_obj_array_2);
            trace_secretary_t.TERMINAL_OUTPUT("bid_invtg.login_ppd_web_sevice(), login result: " + (result.isSucess()? result.getContext():result.getErrorMessage()));
 
			//3rd, refresh tokent
            //ˢ��token
            authInfo = OpenApiClient.refreshToken(authInfo.getOpenID(), authInfo.getRefreshToken());
            System.out.println("���ؽ��: " + authInfo.getAccessToken());
			
		}catch(Exception et){
			trace_secretary_t.LOG_INFO("bid_invtg.login_ppd_web_sevice(), FATAL! register on PPD FAILED! EXCEPTION: " + et);
			trace_secretary_t.TERMINAL_OUTPUT("bid_invtg.login_ppd_web_sevice(), FATAL! register on PPD FAILED! EXCEPTION: " + et);
			return -1;
		}
		
		trace_secretary_t.LOG_INFO("bid_invtg.login_ppd_web_sevice(), quit! ");
		this.set_status(INVESTIGATER_STATUS_LOGIN);
		return 0;
	}
	
	public int get_bid_projects()
	{
		//to get bid information from PPD WEB service,
		//and save into our own list
		return 0;
	}

	public int bid_projects()
	{
		//check all the bid project from our list,
		//and find the best ones to bid
		return 0;
	}
	
	public void run()
	{
		try{
			if(this.login_ppd_web_sevice()!=0)
			{
				trace_secretary_t.LOG_ERROR("bid_invtg.run(), FATAL! register on PPD FAILED! for account: " + this.m_bidder_email_account);
				trace_secretary_t.TERMINAL_OUTPUT("bid_invtg.run(), FATAL! register on PPD FAILED! for account: " + this.m_bidder_email_account);
				m_is_stop = true;
				this.set_status(INVESTIGATER_STATUS_CLOSED);
				return;
			}
			
			while(m_is_stop==false)
			{
				this.set_status(INVESTIGATER_STATUS_RUNNING);
				this.get_bid_projects();
				this.bid_projects();
				Thread.sleep(HEART_BEAT_INTERVAL);				
			}			
		}catch(Exception ep)
		{
			
		}
		this.set_status(INVESTIGATER_STATUS_CLOSED);
	}
}