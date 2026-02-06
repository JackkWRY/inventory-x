
import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { useKeyboardShortcuts } from '../../app/composables/useKeyboardShortcuts';
import { onMounted, onUnmounted } from 'vue';

// Mock Vue lifecycle hooks since we are running outside of a component
vi.mock('vue', async () => {
  const actual = await vi.importActual('vue');
  return {
    ...actual,
    onMounted: vi.fn((fn) => fn()),
    onUnmounted: vi.fn(),
  };
});

describe('useKeyboardShortcuts', () => {
    let addEventListenerSpy: any;
    let removeEventListenerSpy: any;

    beforeEach(() => {
        addEventListenerSpy = vi.spyOn(window, 'addEventListener');
        removeEventListenerSpy = vi.spyOn(window, 'removeEventListener');
    });

    afterEach(() => {
        vi.restoreAllMocks();
    });

    it('should register event listener on mount', () => {
        useKeyboardShortcuts();
        expect(addEventListenerSpy).toHaveBeenCalledWith('keydown', expect.any(Function));
    });

    it('should trigger onSearch callback when / is pressed', () => {
        const onSearch = vi.fn();
        useKeyboardShortcuts({ onSearch });

        const event = new KeyboardEvent('keydown', { key: '/' });
        window.dispatchEvent(event);

        expect(onSearch).toHaveBeenCalled();
    });

    it('should trigger onNew callback when Ctrl+n is pressed', () => {
        const onNew = vi.fn();
        useKeyboardShortcuts({ onNew });

        const event = new KeyboardEvent('keydown', { key: 'n', ctrlKey: true });
        window.dispatchEvent(event);

        expect(onNew).toHaveBeenCalled();
    });

    it('should NOT trigger callbacks if disabled', () => {
        const onSearch = vi.fn();
        useKeyboardShortcuts({ onSearch, enabled: false });

        const event = new KeyboardEvent('keydown', { key: '/' });
        window.dispatchEvent(event);

        expect(onSearch).not.toHaveBeenCalled();
    });
    
    // Simulate input focus to test typing guard
    it('should NOT trigger callbacks if typing in input', () => {
        const onSearch = vi.fn();
        useKeyboardShortcuts({ onSearch });

        const input = document.createElement('input');
        document.body.appendChild(input);
        input.focus();

        const event = new KeyboardEvent('keydown', { key: '/' });
        
        // Mock activeElement logic? 
        // jsdom should handle activeElement if we focus.
        window.dispatchEvent(event);
        
        expect(onSearch).not.toHaveBeenCalled();
        
        document.body.removeChild(input);
    });
});
