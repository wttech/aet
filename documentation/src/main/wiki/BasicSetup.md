### Basic Setup

This setup makes use of the Vagrant module, a pseudo-cookbook which is responsible for local environment provisioning using Vagrant (powered by Chef + Berkshelf under the hood).

#### Overview

Currently a virtual machine with the following services is created:

* Karaf
* Apache
* Tomcat
* ActiveMQ
* MongoDb
* Brosermob
* Firefox
* X environment

#### AET Services

All services run using default ports. For communication please use the following IP address:

* `192.168.123.100`

#### General Prerequisites

By default the Vagrant virtual machine needs 3 GB of RAM and 2 vCPUs, so please make sure that you have enough memory on your machine (8 GB is minimum, 16 GB is recommended though).

#### Installation

* Download and install [VirtualBox 5.2.16](https://www.virtualbox.org/wiki/Downloads)
* Download and install [Vagrant 2.1.2](https://releases.hashicorp.com/vagrant/)
* Download and install [ChefDK 3.1.0](https://downloads.chef.io/chefdk/stable)

As the administrator execute the following commands:

* `vagrant plugin install vagrant-omnibus`
* `vagrant plugin install vagrant-berkshelf`
* `vagrant plugin install vagrant-hostmanager`

Whenever you'd like to keep all Vagrant related data and virtual machine disks in non-standard directories please:

* set the `VAGRANT_HOME` variable for the location (by default it is set to `$HOME/vagrant.d`).
* update VirtualBox settings (`File -> Preferences -> General`) to move all disks to other directory.

#### Starting Virtual Machine

Once you set all the thigs described above just execute:

```
berks update && vagrant destroy -f && vagrant up
```

#### First Run

All the commands need to be executed when you're inside a directory that contains `Vagrantfile`.

Next you will need to execute:

* `berks install` - downloads Chef dependencies from external sources. It acts as `mvn clean install`, but for Chef cookbooks.
* `vagrant up` - creates a new virtual machine (the `.box` file will be downloaded during the first run), runs Chef inside it, sets domains and port forwarding up.

#### Updates

Whenever a new version is released please execute the following:

* `git pull` to get the latest version of `Vagrantfile`.
* `berks update` to update Chef dependencies.
* `vagrant provision` to re-run Chef on the virtual machine.

#### SSH Access

To access the virtual machine via SSH please execute `vagrant ssh` from the same directory that contains `Vagrantfile`. After that please type `sudo -i` and press ENTER to switch to `root`.

If you prefer to use PuTTY, mRemote or any other connection manager, please log in as the user `vagrant` with the password `vagrant` on `localhost` with the port `2222`. Keep in mind that the port may be different if you have more than one Vagrant machine running at the same time. You can check the current assignment by executing the `vagrant ssh-config` command from the directory that contains your `Vagrantfile`.

#### Useful Vagrant Commands

* `vagrant reload` restarts the Vagrant machine and re-applies settings defined in
  `Vagrantfile`. It's useful whenever you've changed the port forwarding or synced
  folder configuration.
* `vagrant destroy -f` deletes the entire virtual machine.
* `vagrant reload --provision` restarts the virtual machine and re-runs Chef
  afterwards.
* `vagrant suspend` suspends the virtual machine that is currently running.
* `vagrant resume` resumes the suspended virtual machine.
* `vagrant status` shows the status of the virtual machine described in `Vagrantfile`.
* `vagrant halt` halts/turns off the virtual machine.

#### Port Forwarding

The local port is a port exposed on your machine. You can access services via `localhost:<PORT>`.

The VM port refers to the port assigned inside the Vagrant virtual machine.

Port forwarding rules can be easily changed in `Vagrantfile`.

| Local port | VM port | Description |
| ---------- | ------- | ----------- |
| 8181       | 8181    | Karaf       |


#### Known Issues

* When getting the following error during application deployment to the local Vagrant:
    ```
    What went wrong: Execution failed for task ':deployDevClearCache'. > java.net.ConnectException: Connection timed out: connect
    ```
    run the `ifup eth1` command on Vagrant using ssh.
