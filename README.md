# boot-s3

[![Dependencies Status](http://jarkeeper.com/hashobject/boot-s3/status.svg)](http://jarkeeper.com/hashobject/boot-s3)

[Boot](http://boot-clj.com/) task to sync local directory with AWS S3.
Most of the code borrowed from [lein-s3-sync](https://github.com/kanej/lein-s3-sync).
This task works in the efficient way and uploads only new or changed files to the s3.

## Install

```
[hashobject/boot-s3 "0.1.0-SNAPSHOT"]
```

## Usage

Add library
```
(require '[hashobject.boot-s3 :refer :all])
```
and use it:

```
boot s3-sync -h
Sync local directory to AWS S3

Options:
  -h, --help                   Print this help info.
  -s, --source PATH            Set source directory to upload to s3 to PATH.
  -b, --bucket BUCKET          Set s3 bucket name to BUCKET.
  -a, --access-key ACCESS_KEY  Set s3 access key to ACCESS_KEY.
  -k, --secret-key SECRET      Set s3 secret key to SECRET.
  -o, --options OPTIONS        Conj OPTIONS onto extra option set for each s3 file
```

## Contributions

We love contributions. Please submit your pull requests.


## License

Copyright © 2013-2015 Hashobject Ltd (team@hashobject.com).

Distributed under the [Eclipse Public License](http://opensource.org/licenses/eclipse-1.0).
