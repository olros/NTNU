extern crate nalgebra_glm as glm;
use std::f64::consts::PI;

pub struct Heading {
    pub x     : f32,
    pub z     : f32,
    pub roll  : f32, // measured in radians
    pub pitch : f32, // measured in radians
    pub yaw   : f32, // measured in radians
}

pub fn simple_heading_animation(time: f32) -> Heading {
    let t             = time as f64;
    let step          = 0.05f64;
    let path_size     = 15f64;
    let circuit_speed = 0.8f64;

    let xpos      = path_size * (2.0 * (t+ 0.0) * circuit_speed).sin();
    let xpos_next = path_size * (2.0 * (t+step) * circuit_speed).sin();
    let zpos      = 3.0 * path_size * ((t+ 0.0) * circuit_speed).cos();
    let zpos_next = 3.0 * path_size * ((t+step) * circuit_speed).cos();

    let delta_pos = glm::vec2(xpos_next - xpos, zpos_next - zpos);

    let roll  = (t * circuit_speed).cos() * 0.5;
    let pitch = -0.175 * glm::length(&delta_pos);
    let yaw   = PI + delta_pos.x.atan2(delta_pos.y);

    Heading {
        x     : xpos  as f32,
        z     : zpos  as f32,
        roll  : roll  as f32,
        pitch : pitch as f32,
        yaw   : yaw   as f32,
    }
}
