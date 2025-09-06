import { createSlice } from '@reduxjs/toolkit';

interface UserState {
  // This will be expanded later
  placeholder: boolean;
}

const initialState: UserState = {
  placeholder: true,
};

export const userSlice = createSlice({
  name: 'user',
  initialState,
  reducers: {
    // Placeholder reducer
    setPlaceholder: (state, action) => {
      state.placeholder = action.payload;
    },
  },
});

export const { setPlaceholder } = userSlice.actions;
export default userSlice.reducer;
