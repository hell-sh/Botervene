# Configuration

Just a little bit of a documentation on the `config.json`.

- `log_file` is the path to the log file that Botervene is supposed to monitor. Usually that will just be the `botervene.log` you will have configured in the Setup.
- `visitors_journal` is the file Botervene will use to keep track of all visitors and bans so Botervene can be restarted once in a while without any issues.
- `cf_email` is your Cloudflare Account Email.
- `cf_key` is your Cloudflare API Key. You can find it as at https://www.cloudflare.com/a/profile where it is referred to as Global API Key.
- `firewall_action` is the action that Cloudflare will perform. Valid values: block, challenge, js_challenge
- `ban_duration` is the amount of seconds Botervene will wait until removing the firewall rule again.
