/***
 * this interface defined all the APIs a PPD client would call back
 * 
 * @author Adam Wang
 * @date   2017-Oct-8th
 *
 */


public interface ppd_client_sink_it
{
	void on_status_update_indication();
	void on_running_log_indication();
	void on_command_responed(int command_type);
}