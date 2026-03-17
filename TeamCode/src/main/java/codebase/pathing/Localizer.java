package codebase.pathing;

import codebase.Loop;
import codebase.geometry.FieldPosition;

public interface Localizer extends Loop {

    FieldPosition getCurrentPosition();

    void init(FieldPosition initialPosition);
}
