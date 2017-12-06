## Installing on Openshift

The following are the instructions for installing **openfact-sync** on Openshift. In this example I'll use Minishift, in your case you can use your own Openshift Cluster:


* Make sure you have a recent (3.5 of openshift or 1.5 of origin later) distribution of the `oc` binary on your `$PATH`
```
oc version
```
* If you have an old version or its not found please [download a distribution of the openshift-client-tools for your operating system](https://github.com/openshift/origin/releases/latest/) and copy the `oc` binary onto your `$PATH`

* [download the minishift distribution for your platform](https://github.com/minishift/minishift/releases) extract it and place the `minishift` binary on your `$PATH` somewhere
* start up minishift via something like this (on OS X):

```
minishift start --vm-driver=xhyve --memory=7000 --cpus=4 --disk-size=50g
```
or on any other operating system (feel free to add the `--vm-driver` parameter of your choosing):

```
minishift start --memory=7000 --cpus=4 --disk-size=50g
```

## Setup Google Client
To configure Google Client follow the steps described in [Google Client Setup](https://github.com/openfact/openfact-sync/blob/master/docs/openshift_google_client.md).

## Setup Elasticsearch - Experimental (Optional)
By Default **openfact-sync** use Lucene file store. Nevertheless, we encourage you to use **Elasticsearch** Cluster. To install elasticsearch on Openshift follow the steps described in [Elasticsearch Setup](https://github.com/openfact/openfact-sync/blob/master/docs/openshift_elasticsearch.md). 

## Setup Smtp Server - Development environment only (Optional)
To enable email sends, you need to configure a SMTP Server like Gmail from Google for instance. For development purposes you can configure a [SMTP server](https://github.com/openfact/openfact-sync/blob/master/docs/openshift_smtp_server.md).


## Run the install script
Make sure you are on the project folder.

* now run the [install.sh](https://raw.githubusercontent.com/openfact/openfact-sync/master/scripts/install.sh) script on the command line:

```
$ ./scripts/install.sh
```

or

* Run the [install.sh](https://raw.githubusercontent.com/openfact/openfact-sync/master/scripts/install.sh) script on the command line:

```
bash <(curl -s https://raw.githubusercontent.com/openfact/openfact-sync/master/scripts/install.sh)
```

* if you want to install a specific version of the [openfact sync template](http://central.maven.org/maven2/io/openfact/platform/packages/openfact-system/) then you can pass it on the command line as an argument. Or add the argument `local` to use a local build.


## Accept the insecure URLs in your browser

Currently there are 4 different URLS that Chrome will barf on and you'll have to explcitily click on the `ADVANCED` button then click on the URL to tell your browser its fine to trust the URLs before you can open and use the new fabric8 console

The above script should list the 4 URLs you need to open separately and approve.

We hope to figure out a nicer alternative to this issue! The problem is things like lenscript only work for public hosted URLs; whereas running locally on MiniShift we're local but use `nip.io` to provide a global URL to your local machine (to simplify having to do DNS magic on your laptop). If you fancy trying to help fix this [please check out this MiniShift issue](https://github.com/minishift/minishift/issues/1031)
