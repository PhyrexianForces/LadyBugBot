package codebase.pathing;

import codebase.Loop;
import codebase.geometry.FieldPosition;

/**
 * Team 4096 Localization System
 * X-positive: towards audience viewing area
 * Y-positive: towards right of field
 * 0 rotation: facing X-positive direction?? maybe???????
 */
public interface Localizer extends Loop {

    FieldPosition getCurrentPosition();

    void init();
}
