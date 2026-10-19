jHashtools![Project status](http://stillmaintained.com/nicoulaj/maven-zsh-completion.png)
=========================================================================================
v1.2, Erwin van Eijk<erwin.vaneijk@gmail.com>

## Contributing

Use **Conventional Commits** for commit messages (for example:
`feat(scope): short description`, `fix: short description`).
See `CONTRIBUTING.md` for full contribution and commit guidelines.

History
-------
jHashtools is the java-port of a tool that was developed a couple of years ago
for computing the digests of a complete directory tree. It could also take the
output of a previous run and check if all the files mentioned in the file were
contained in the directory and vice-versa. This tool was implemented in python.
Although python is a quite beautiful language, it contained a lot of glued-on
functionality which was quite hard to maintain.

This tool
---------
This tool does the same as the above mentioned use case. It was implemented in
java because that's the language that's used most at the moment and it strikes
are quite good balance between ease of writing and speed.

Building
--------
Building requires JDK 25 or newer (`JAVA_HOME` must point to it); Maven is
provided by the Maven Wrapper:

    ./mvnw clean verify

This runs the tests, coverage, formatting and static analysis checks and
produces the executable jar `target/jHashtools-<version>.jar`. See
CONTRIBUTING.md for details.

Usage
-----
    java -jar target/jHashtools-1.2.jar --help
    java -jar target/jHashtools-1.2.jar -2 -s json -o digests.json <dir>
    java -jar target/jHashtools-1.2.jar -2 -s json -i digests.json <dir>

Forensic use
------------
jHashtools only reads the files it hashes, so it never changes their
modification time (mtime) or inode change time (ctime). Reading a file can,
however, update its last access time (atime), and listing a directory can
update the directory's atime.

### Preferred: make the evidence read-only

Read-only access to the evidence is the only reliable way to leave every
timestamp untouched. Do it below the tool rather than relying on the tool:

- Attach the original media through a hardware write blocker, or
- hash a forensic image mounted read-only, for example:
  - Linux: `mount -o ro,loop,noatime,noexec image.raw /mnt/evidence`
    (add `offset=...` for a partition in a disk image, and `noload` for
    ext3/ext4 so the journal is not replayed onto the image)
  - macOS: `hdiutil attach -readonly -imagekey diskimage-class=CRawDiskImage image.raw`

With `noatime` or read-only media, reading the files cannot update their
access times.

### What jHashtools records

For every hashed file, jHashtools reads the atime just before and just after
reading the content, without following symbolic links. Reading these
attributes does not change them. The observed times are written to the JSON
output (`-s json`, the default) next to the digests:

    "accessTimes": {
        "evidence/a.txt": {
            "before": "2001-02-03T03:05:00Z",
            "after": "2026-10-19T08:36:01.321444080Z"
        }
    }

When hashing (`-o`) or verifying (`-i`) changes an access time, jHashtools
logs a warning naming the file and both times. The old-style format (`-s old`)
does not include access times.

Limitations:

- An unchanged atime does not prove the file was not read. With Linux
  `relatime` (the default) the atime is only updated when it is older than
  the mtime or ctime, or more than a day old; macOS behaves similarly.
- Directory access times are not recorded. Listing a directory can still
  update its atime.
- jHashtools does not restore access times afterwards. On POSIX systems,
  setting the atime updates the ctime, which user space cannot reset, so
  restoring would trade one changed timestamp for another.

### The output file

Writing the digest file into the tree being hashed would add a file and change
the directory's mtime and ctime. jHashtools therefore refuses (exit status
`1`) when the `-o` path, after resolving symbolic links, is an input file or
lies inside an input directory. Write it to separate case storage instead:

    java -jar target/jHashtools-1.2.jar -o /cases/1234/digests.json /mnt/evidence

Exit status
-----------
- `0`: the digests were written, or verification passed.
- `3`: verification (`-i`) found differences.
- Any other non-zero value: an error, such as invalid options or an unreadable
  digest file.
