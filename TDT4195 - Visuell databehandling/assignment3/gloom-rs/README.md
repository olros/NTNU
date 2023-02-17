# Gloom-rs

To get started, make sure you have `git`, `cargo` and, `rustc` installed and available.

	git clone https://github.com/pbsds/gloom-rs
	cd gloom-rs
	rustup override set nightly
	cargo run


## GLM

We use a variant of GLM known as [nalgebra-glm](https://docs.rs/nalgebra-glm/0.15.0/nalgebra_glm/), which differs *slightly* from the standard GLM library.


## Report

You're free to write your report any way you'd like, as long as it is delivered as a PDF file.

To spread the gospel, I have included a `pandoc` report skeleton in the `report` folder.
To use pandoc, make sure you have `pandoc` installed along with a supported latex engine.
Make sure it works before using it to write your report.

## Code delivery

We want the following files and folders to be delivered in a ZIP file:

* `resources`
* `shaders`
* `src`
* `Cargo.lock`
* `Cargo.toml`

**Important:** Do not include the `target` folder!

To automatically make an archive (`source.zip`) ready for uploading to blackboard:

* Make sure any extra assets or resources you have might have added are located in the `resources` folder
* Then run either:
	* `create_code_archive_for_blackboard_LINUX.sh`
	* `create_code_archive_for_blackboard_WINDOWS.bat`.

This script will explicitly ignore the `target` folder, and two files given as a handout for exercise 3, to save space:

* `resources/helicopter.obj`
* `resources/lunarsurface.obj`
