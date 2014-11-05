<!-- --- title: Installation -->

<!-- SIMPLE HORIZONTAL NAVIGATOR -->
<div class="horizontal-navigator">
	<span>
		Next: <a href="./02-Launching">Launching</a>
	</span>
</div>

The installation process is divided into 2 parts: installing RVM and installing Gollum. The first can be skipped if you don't plan on using many different ruby versions/implementations/gemsets/etc on your machine. If that is the case, simply install a compatible Ruby implementation (version 1.9.3 is recommended by Gollum) using your system's package manager and proceed to installing Gollum.




## Install & setup RVM

Open your shell.

### Install

`curl -sSL https://get.rvm.io | bash -s stable`

### RVM needs to be a function

When installing RVM in the previous step something like the following should have been printed:  
_To start using RVM you need to run `source $HOME/.rvm/scripts/rvm`
in all your open shell windows, in rare cases you need to reopen all shell windows_.  

Some lines of code should have been added to your `.profile` and `.bash_profile` (located in your user's `$HOME` dir). They have been added there so you don't have to enter the commands over and over again when creating a shell session/window. As requested by the program, enter:  

1. `source $HOME/.profile`
2. `source $HOME/.bash_profile`

After you have done so, confirm that RVM is a function. Run:  
`type rvm | head -n 1`

The following should be printed in response:  
`rvm is a function`

### Install general Ruby implementation and use it

`rvm install 1.9.3`  
`rvm use 1.9.3`

Notes:
* Gollum should be compatible with newer Ruby versions and you can use them but 1.9.3 is recommended so we will use it.
* When opening a new shell window, RVM will use the "default" Ruby. To avoid accidental confusions and possibly even errors while setting up / maintaining Gollum, you should set the Ruby you wish to use with Gollum as default, e.g. `rvm use 1.9.3 --default`.

### Create a new gemset for the installed Ruby

For more information as to what a gemset is, take a look at internet or RVM website. Enter:  
`rvm gemset create {my-gemset}`  
`rvm gemset use {my-gemset} --default`

<font color="red">NOTE:</font> never use sudo when working with gemsets. If you do, you are running commands as another user in another shell and hence:
* all of the setup that RVM has done for you is ignored,
* your environment will be cleared out,
* files created will be unmodifiable by your user account and strange things will happen (you will start to think that someone has a voodoo doll of your application).




## Install Gollum

This step is pretty simple.

1. Install Python, use your favourite package manager. Version >= 2.5 is required, 2.7.3 recommended.
2. `gem install gollum`
3. `gem install asciidoctor` - adds asciidoc format support
4. `gem install redcarpet` - adds markdown format support
5. `gem install wikicloth` - adds mediawiki format support




## Tips & tricks

### Commands

`which ruby`  
Echos path to the currently used ruby.

`gem list {gem} --remote --all`  
Finds all available versions of the specified gem.

`gem install {gem} -v {version}`  
Installs arbitrary version of a gem.

`rvm gemdir`  
Gem directory of the currently selected ruby.

`rvm gemset name`  
Currently selected gemset's name.

`rvm gemset list`  
All created gemsets for the currently selected ruby.

### Links

<https://rvm.io/rubies/installing>  
<https://rvm.io/rubies/default>  
<https://rvm.io/rvm/basics>  
<https://rvm.io/rvm/configuration>  
<https://rvm.io/rvm/best-practices>  
<https://rvm.io/workflow/rvmrc>  
<https://rvm.io/gemsets/using>  

### Miscellaneous

* RVM creates a new completely separate gem directory for each version of ruby and gemset, so install away :).
* If you are deploying to a server or you don't want to wait around for rdoc and ri to install for each gem, you can disable them for gem installs and updates. Just add the following line to your `~/.gemrc or /etc/gemrc`:  
`gem: --no-rdoc --no-ri`