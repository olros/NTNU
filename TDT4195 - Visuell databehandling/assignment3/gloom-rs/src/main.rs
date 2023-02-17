#![allow(dead_code)]
#![allow(non_snake_case)]
#![allow(unreachable_code)]
#![allow(unused_mut)]
#![allow(unused_unsafe)]
#![allow(unused_variables)]
extern crate nalgebra_glm as glm;
use std::sync::{Arc, Mutex, RwLock};
use std::thread;
use std::{mem, os::raw::c_void, ptr};

mod mesh;
mod scene_graph;
mod shader;
mod toolbox;
mod util;

use glutin::event::{
    DeviceEvent,
    ElementState::{Pressed, Released},
    Event, KeyboardInput,
    VirtualKeyCode::{self, *},
    WindowEvent,
};
use glutin::event_loop::ControlFlow;

struct TripodCameraPosition {
    rotate_x: f32,
    rotate_y: f32,
    x: f32,
    y: f32,
    z: f32,
}

impl TripodCameraPosition {
    fn position(&self) -> glm::Vec3 {
        glm::vec3(self.x, self.y, self.z)
    }
    fn rotation(&self) -> glm::Vec2 {
        glm::vec2(self.rotate_x, self.rotate_y)
    }
}

// initial window size
const INITIAL_SCREEN_W: u32 = 800;
const INITIAL_SCREEN_H: u32 = 600;

// == // Helper functions to make interacting with OpenGL a little bit prettier. You *WILL* need these! // == //

// Get the size of an arbitrary array of numbers measured in bytes
// Example usage:  pointer_to_array(my_array)
fn byte_size_of_array<T>(val: &[T]) -> isize {
    std::mem::size_of_val(&val[..]) as isize
}

// Get the OpenGL-compatible pointer to an arbitrary array of numbers
// Example usage:  pointer_to_array(my_array)
fn pointer_to_array<T>(val: &[T]) -> *const c_void {
    &val[0] as *const T as *const c_void
}

// Get the size of the given type in bytes
// Example usage:  size_of::<u64>()
fn size_of<T>() -> i32 {
    mem::size_of::<T>() as i32
}

// Get an offset in bytes for n units of type T, represented as a relative pointer
// Example usage:  offset::<u64>(4)
fn offset<T>(n: u32) -> *const c_void {
    (n * mem::size_of::<T>() as u32) as *const T as *const c_void
}

// == // Generate your VAO here
unsafe fn create_vao(
    vertices: &Vec<f32>,
    indices: &Vec<u32>,
    colors: &Vec<f32>,
    normals: &Vec<f32>,
) -> u32 {
    // Generate a VAO and bind it
    let mut VAO: u32 = 0;
    gl::GenVertexArrays(1, &mut VAO);
    gl::BindVertexArray(VAO);

    // Generate a VBO, bind it and fill it with data
    let mut VBO: u32 = 0;
    gl::GenBuffers(1, &mut VBO);
    gl::BindBuffer(gl::ARRAY_BUFFER, VBO);
    gl::BufferData(
        gl::ARRAY_BUFFER,
        byte_size_of_array(vertices),
        pointer_to_array(vertices),
        gl::STATIC_DRAW,
    );

    // Configure a VAP for the data and enable it
    gl::VertexAttribPointer(0, 3, gl::FLOAT, gl::FALSE, 0, ptr::null());
    gl::EnableVertexAttribArray(0);

    // Generate a CBO, bind it and fill it with data
    let mut CBO: u32 = 0;
    gl::GenBuffers(1, &mut CBO);
    gl::BindBuffer(gl::ARRAY_BUFFER, CBO);
    gl::BufferData(
        gl::ARRAY_BUFFER,
        byte_size_of_array(colors),
        pointer_to_array(colors),
        gl::STATIC_DRAW,
    );
    gl::VertexAttribPointer(1, 4, gl::FLOAT, gl::FALSE, 0, ptr::null());
    gl::EnableVertexAttribArray(1);

    // Generate a Normals Buffer Object, bind it and fill it with data
    let mut NBO: u32 = 0;
    gl::GenBuffers(1, &mut NBO);
    gl::BindBuffer(gl::ARRAY_BUFFER, NBO);
    gl::BufferData(
        gl::ARRAY_BUFFER,
        byte_size_of_array(normals),
        pointer_to_array(normals),
        gl::STATIC_DRAW,
    );
    gl::VertexAttribPointer(3, 3, gl::FLOAT, gl::FALSE, 0, ptr::null());
    gl::EnableVertexAttribArray(3);

    // Generate a IBO, bind it and fill it with data
    let mut IBO: u32 = 0;
    gl::GenBuffers(1, &mut IBO);
    gl::BindBuffer(gl::ELEMENT_ARRAY_BUFFER, IBO);
    gl::BufferData(
        gl::ELEMENT_ARRAY_BUFFER,
        byte_size_of_array(indices),
        pointer_to_array(indices),
        gl::STATIC_DRAW,
    );

    // Return the ID of the VAO
    VAO
}

unsafe fn draw_scene(
    node: &scene_graph::SceneNode,
    view_projection_matrix: &glm::Mat4,
    transformation_so_far: &glm::Mat4,
) {
    // Check if node is drawable, if so: set uniforms and draw
    // Construct the correct transformation matrix
    let origin: glm::Mat4 = glm::translation(&node.reference_point);
    let inverse_origin: glm::Mat4 = glm::inverse(&origin);
    let position: glm::Mat4 = glm::translation(&node.position);

    let x_rotation: glm::Mat4 = glm::rotation(node.rotation[0], &glm::vec3(1.0, 0.0, 0.0));
    let y_rotation: glm::Mat4 = glm::rotation(node.rotation[1], &glm::vec3(0.0, 1.0, 0.0));
    let z_rotation: glm::Mat4 = glm::rotation(node.rotation[2], &glm::vec3(0.0, 0.0, 1.0));
    // Update the node's transformation matrix
    let current_transformation_matrix = transformation_so_far
        * position
        * origin
        * z_rotation
        * y_rotation
        * x_rotation
        * inverse_origin;

    if node.index_count > 0 {
        gl::UniformMatrix4fv(
            2,
            1,
            0,
            (view_projection_matrix * current_transformation_matrix).as_ptr(),
        );
        gl::UniformMatrix4fv(4, 1, 0, (current_transformation_matrix).as_ptr());
        gl::BindVertexArray(node.vao_id);
        gl::DrawElements(
            gl::TRIANGLES,
            node.index_count,
            gl::UNSIGNED_INT,
            ptr::null(),
        );
    }

    // Recurse
    for &child in &node.children {
        draw_scene(
            &*child,
            view_projection_matrix,
            &current_transformation_matrix,
        );
    }
}

fn main() {
    // Set up the necessary objects to deal with windows and event handling
    let el = glutin::event_loop::EventLoop::new();
    let wb = glutin::window::WindowBuilder::new()
        .with_title("Gloom-rs")
        .with_resizable(true)
        .with_inner_size(glutin::dpi::LogicalSize::new(
            INITIAL_SCREEN_W,
            INITIAL_SCREEN_H,
        ));
    let cb = glutin::ContextBuilder::new().with_vsync(true);
    let windowed_context = cb.build_windowed(wb, &el).unwrap();
    // Uncomment these if you want to use the mouse for controls, but want it to be confined to the screen and/or invisible.
    // windowed_context.window().set_cursor_grab(true).expect("failed to grab cursor");
    // windowed_context.window().set_cursor_visible(false);

    // Set up a shared vector for keeping track of currently pressed keys
    let arc_pressed_keys = Arc::new(Mutex::new(Vec::<VirtualKeyCode>::with_capacity(10)));
    // Make a reference of this vector to send to the render thread
    let pressed_keys = Arc::clone(&arc_pressed_keys);

    // Set up shared tuple for tracking mouse movement between frames
    let arc_mouse_delta = Arc::new(Mutex::new((0f32, 0f32)));
    // Make a reference of this tuple to send to the render thread
    let mouse_delta = Arc::clone(&arc_mouse_delta);

    // Set up shared tuple for tracking changes to the window size
    let arc_window_size = Arc::new(Mutex::new((INITIAL_SCREEN_W, INITIAL_SCREEN_H, false)));
    // Make a reference of this tuple to send to the render thread
    let window_size = Arc::clone(&arc_window_size);

    // Spawn a separate thread for rendering, so event handling doesn't block rendering
    let render_thread = thread::spawn(move || {
        // Acquire the OpenGL Context and load the function pointers.
        // This has to be done inside of the rendering thread, because
        // an active OpenGL context cannot safely traverse a thread boundary
        let context = unsafe {
            let c = windowed_context.make_current().unwrap();
            gl::load_with(|symbol| c.get_proc_address(symbol) as *const _);
            c
        };

        let mut window_aspect_ratio = INITIAL_SCREEN_W as f32 / INITIAL_SCREEN_H as f32;

        // Set up openGL
        unsafe {
            gl::Enable(gl::DEPTH_TEST);
            gl::DepthFunc(gl::LESS);
            gl::Enable(gl::CULL_FACE);
            gl::Disable(gl::MULTISAMPLE);
            gl::Enable(gl::BLEND);
            gl::BlendFunc(gl::SRC_ALPHA, gl::ONE_MINUS_SRC_ALPHA);
            gl::Enable(gl::DEBUG_OUTPUT_SYNCHRONOUS);
            gl::DebugMessageCallback(Some(util::debug_callback), ptr::null());

            // Print some diagnostics
            println!(
                "{}: {}",
                util::get_gl_string(gl::VENDOR),
                util::get_gl_string(gl::RENDERER)
            );
            println!("OpenGL\t: {}", util::get_gl_string(gl::VERSION));
            println!(
                "GLSL\t: {}",
                util::get_gl_string(gl::SHADING_LANGUAGE_VERSION)
            );
        }

        // == // Set up your VAO around here
        let terrain: mesh::Mesh = mesh::Terrain::load("./resources/lunarsurface.obj");
        let terrain_vao = unsafe {
            create_vao(
                &terrain.vertices,
                &terrain.indices,
                &terrain.colors,
                &terrain.normals,
            )
        };

        let helicopter: mesh::Helicopter = mesh::Helicopter::load("./resources/helicopter.obj");
        let helicopter_body_vao = unsafe {
            create_vao(
                &helicopter.body.vertices,
                &helicopter.body.indices,
                &helicopter.body.colors,
                &helicopter.body.normals,
            )
        };
        let helicopter_door_vao = unsafe {
            create_vao(
                &helicopter.door.vertices,
                &helicopter.door.indices,
                &helicopter.door.colors,
                &helicopter.door.normals,
            )
        };
        let helicopter_main_rotor_vao = unsafe {
            create_vao(
                &helicopter.main_rotor.vertices,
                &helicopter.main_rotor.indices,
                &helicopter.main_rotor.colors,
                &helicopter.main_rotor.normals,
            )
        };
        let helicopter_tail_rotor_vao = unsafe {
            create_vao(
                &helicopter.tail_rotor.vertices,
                &helicopter.tail_rotor.indices,
                &helicopter.tail_rotor.colors,
                &helicopter.tail_rotor.normals,
            )
        };

        let mut root_node = scene_graph::SceneNode::new();
        let mut terrain_node = scene_graph::SceneNode::from_vao(terrain_vao, terrain.index_count);

        for _x in 0..7 {
            let mut helicopter_body_node =
                scene_graph::SceneNode::from_vao(helicopter_body_vao, helicopter.body.index_count);
            helicopter_body_node.add_child(&scene_graph::SceneNode::from_vao(
                helicopter_door_vao,
                helicopter.door.index_count,
            ));
            helicopter_body_node.add_child(&scene_graph::SceneNode::from_vao(
                helicopter_main_rotor_vao,
                helicopter.main_rotor.index_count,
            ));
            helicopter_body_node.add_child(&scene_graph::SceneNode::from_vao(
                helicopter_tail_rotor_vao,
                helicopter.tail_rotor.index_count,
            ));
            helicopter_body_node[2].reference_point = glm::vec3(0.35, 2.3, 10.4);
            terrain_node.add_child(&helicopter_body_node);
        }

        root_node.add_child(&terrain_node);

        // == // Set up your shaders here
        unsafe {
            let shader = shader::ShaderBuilder::new()
                .attach_file("./shaders/simple.vert")
                .attach_file("./shaders/simple.frag")
                .link();
            shader.activate();
        }

        // Used to demonstrate keyboard handling for exercise 2.
        let mut _arbitrary_number = 0.0; // feel free to remove

        // The main rendering loop
        let first_frame_time = std::time::Instant::now();
        let mut prevous_frame_time = first_frame_time;

        let mut tripod_camera_position = TripodCameraPosition {
            rotate_x: 0.0,
            rotate_y: 0.0,
            x: 0.0,
            y: 0.0,
            z: -2.0,
        };
        let mut door_is_open = false;

        loop {
            // Compute time passed since the previous frame and since the start of the program
            let now = std::time::Instant::now();
            let elapsed = now.duration_since(first_frame_time).as_secs_f32();
            let delta_time = now.duration_since(prevous_frame_time).as_secs_f32();
            prevous_frame_time = now;

            // Handle resize events
            if let Ok(mut new_size) = window_size.lock() {
                if new_size.2 {
                    context.resize(glutin::dpi::PhysicalSize::new(new_size.0, new_size.1));
                    window_aspect_ratio = new_size.0 as f32 / new_size.1 as f32;
                    (*new_size).2 = false;
                    println!("Resized");
                    unsafe {
                        gl::Viewport(0, 0, new_size.0 as i32, new_size.1 as i32);
                    }
                }
            }

            // Handle keyboard input
            if let Ok(keys) = pressed_keys.lock() {
                for key in keys.iter() {
                    match key {
                        VirtualKeyCode::W => {
                            // Move forwards in z-direction
                            tripod_camera_position.z += delta_time * 15.0;
                        }
                        VirtualKeyCode::A => {
                            //Move towards left in x-direction
                            tripod_camera_position.x += delta_time * 15.0;
                        }
                        VirtualKeyCode::S => {
                            // Move backwards in z-direction
                            tripod_camera_position.z -= delta_time * 15.0;
                        }
                        VirtualKeyCode::D => {
                            // Move towards right in x-direction
                            tripod_camera_position.x -= delta_time * 15.0;
                        }
                        VirtualKeyCode::Space => {
                            // Move upwards in y-direction
                            tripod_camera_position.y -= delta_time * 15.0;
                        }
                        VirtualKeyCode::LShift => {
                            // Move downwards in y-direction
                            tripod_camera_position.y += delta_time * 15.0;
                        }
                        VirtualKeyCode::Up => {
                            // Rotates up in x-direction
                            tripod_camera_position.rotate_x -= delta_time * 5.0;
                        }
                        VirtualKeyCode::Down => {
                            // Rotates down in x-direction
                            tripod_camera_position.rotate_x += delta_time * 5.0;
                        }
                        VirtualKeyCode::Left => {
                            // Rotates left in y-direction
                            tripod_camera_position.rotate_y -= delta_time * 5.0;
                        }
                        VirtualKeyCode::Right => {
                            // Rotates rigth in y-direction
                            tripod_camera_position.rotate_y += delta_time * 5.0;
                        }
                        VirtualKeyCode::E => {
                            door_is_open = true;
                        }
                        VirtualKeyCode::R => {
                            door_is_open = false;
                        }
                        _ => {}
                    }
                }
            }
            // Handle mouse movement. delta contains the x and y movement of the mouse since last frame in pixels
            if let Ok(mut delta) = mouse_delta.lock() {
                *delta = (0.0, 0.0); // reset when done
            }

            unsafe {
                // Clear the color and depth buffers
                gl::ClearColor(0.035, 0.046, 0.078, 1.0); // night sky, full opacity
                gl::Clear(gl::COLOR_BUFFER_BIT | gl::DEPTH_BUFFER_BIT);

                let perspective_transform: glm::Mat4 = glm::perspective(1.0, 1.0, 1.0, 1000.0);
                let translation: glm::Mat4 = glm::translation(&tripod_camera_position.position());
                let x_rotation_transform: glm::Mat4 =
                    glm::rotation(tripod_camera_position.rotate_x, &glm::vec3(1.0, 0.0, 0.0));
                let y_rotation_transform: glm::Mat4 =
                    glm::rotation(tripod_camera_position.rotate_y, &glm::vec3(0.0, 1.0, 0.0));

                let matrix: glm::Mat4 = perspective_transform
                    * x_rotation_transform
                    * y_rotation_transform
                    * translation;

                for i in 0..terrain_node.get_n_children() {
                    // Main rotor
                    terrain_node.get_child(i).get_child(1).rotation.y += delta_time * 4.0;
                    // Tail rotor
                    terrain_node.get_child(i).get_child(2).rotation.x += delta_time * 4.0;

                    let door = terrain_node.get_child(i).get_child(0);
                    if door_is_open {
                        if door.position.z <= 2.0 {
                            door.position.z += 0.1;
                        }
                    } else {
                        if door.position.z >= 0.0 {
                            door.position.z -= 0.1;
                        }
                    }

                    let heading = toolbox::simple_heading_animation(elapsed + i as f32);
                    terrain_node.get_child(i).position =
                        glm::vec3(heading.x, terrain_node[i].position.y, heading.z);
                    terrain_node.get_child(i).rotation =
                        glm::vec3(heading.pitch, heading.yaw, heading.roll);
                }

                draw_scene(&root_node, &matrix, &glm::identity());
            }

            // Display the new color buffer on the display
            context.swap_buffers().unwrap(); // we use "double buffering" to avoid artifacts
        }
    });

    // == //
    // == // From here on down there are only internals.
    // == //

    // Keep track of the health of the rendering thread
    let render_thread_healthy = Arc::new(RwLock::new(true));
    let render_thread_watchdog = Arc::clone(&render_thread_healthy);
    thread::spawn(move || {
        if !render_thread.join().is_ok() {
            if let Ok(mut health) = render_thread_watchdog.write() {
                println!("Render thread panicked!");
                *health = false;
            }
        }
    });

    // Start the event loop -- This is where window events are initially handled
    el.run(move |event, _, control_flow| {
        *control_flow = ControlFlow::Wait;

        // Terminate program if render thread panics
        if let Ok(health) = render_thread_healthy.read() {
            if *health == false {
                *control_flow = ControlFlow::Exit;
            }
        }

        match event {
            Event::WindowEvent {
                event: WindowEvent::Resized(physical_size),
                ..
            } => {
                println!(
                    "New window size! width: {}, height: {}",
                    physical_size.width, physical_size.height
                );
                if let Ok(mut new_size) = arc_window_size.lock() {
                    *new_size = (physical_size.width, physical_size.height, true);
                }
            }
            Event::WindowEvent {
                event: WindowEvent::CloseRequested,
                ..
            } => {
                *control_flow = ControlFlow::Exit;
            }
            // Keep track of currently pressed keys to send to the rendering thread
            Event::WindowEvent {
                event:
                    WindowEvent::KeyboardInput {
                        input:
                            KeyboardInput {
                                state: key_state,
                                virtual_keycode: Some(keycode),
                                ..
                            },
                        ..
                    },
                ..
            } => {
                if let Ok(mut keys) = arc_pressed_keys.lock() {
                    match key_state {
                        Released => {
                            if keys.contains(&keycode) {
                                let i = keys.iter().position(|&k| k == keycode).unwrap();
                                keys.remove(i);
                            }
                        }
                        Pressed => {
                            if !keys.contains(&keycode) {
                                keys.push(keycode);
                            }
                        }
                    }
                }

                // Handle Escape and Q keys separately
                match keycode {
                    Escape => {
                        *control_flow = ControlFlow::Exit;
                    }
                    Q => {
                        *control_flow = ControlFlow::Exit;
                    }
                    _ => {}
                }
            }
            Event::DeviceEvent {
                event: DeviceEvent::MouseMotion { delta },
                ..
            } => {
                // Accumulate mouse movement
                if let Ok(mut position) = arc_mouse_delta.lock() {
                    *position = (position.0 + delta.0 as f32, position.1 + delta.1 as f32);
                }
            }
            _ => {}
        }
    });
}
