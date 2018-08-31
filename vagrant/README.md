![Cognifide logo](http://cognifide.github.io/images/cognifide-logo.png)

# AET
<p align="center">
  <img src="https://github.com/Cognifide/aet/blob/master/misc/img/aet-logo-black.png?raw=true"
         alt="AET Logo"/>
</p>

## Vagrant

This is a pseudo-cookbook which is responsible for local environment
provisioning using Vagrant (powered by Chef + Berkshelf under the hood)

### Installation

See [Basic Setup](https://github.com/Cognifide/aet/wiki/BasicSetup) guide.

### Updates

Whenever new version is released please execute the following:

* `git pull` to get latest version of `Vagrantfile` from Stash
* `berks update` to update Chef dependencies
* `vagrant provision` to re-run Chef on the virtual machine

### SSH access

To get into the virtual machine via SSH please execute `vagrant ssh` from the
same directory that contains `Vagrantfile`. After that please type `sudo -i`
and press ENTER to switch to `root`.

If you prefer to use PuTTY, mRemote or any other connection manager, please log
in as user `vagrant` with password `vagrant` on `localhost` port `2222`. Keep
in mind that the port may be different if you have more than one Vagrant
machine running at the same time. You can check current assignment by executing
`vagrant ssh-config` command from directory that contains your `Vagrantfile`.

### Useful Vagrant commands

* `vagrant reload` restarts Vagrant machine and re-applies settings defined in
  `Vagrantfile`. It's useful whenever you've changed port forwarding or synced
  folder configuration
* `vagrant destroy -f` deletes entire virtual machine
* `vagrant reload --provision` restarts virtual machine and re-run Chef
  afterwards
* `vagrant suspend` suspends currently running virtual machine
* `vagrant resume` resumes suspended virtual machine
* `vagrant status` show status of virtual machine described in `Vagrantfile`
* `vagrant halt` halts/turns off virtual machine

### Port forwarding

Local port (*host*) is a port exposed on your machine. You can access services
via `localhost:<PORT>`.

*guest* ports refer to ports assigned inside Vagrant's virtual machine.

Port forwarding rules can be easily changed in `Vagrantfile`.

### Known Issues

* When getting following error on deploying application to local vagrant: `What went wrong: Execution failed for task ':deployDevClearCache'. > java.net.ConnectException: Connection timed out: connect` Run `ifup eth1` command on vagrant using ssh.

### Authors

Karol Drazek (<karol.drazek@cognifide.com>)
