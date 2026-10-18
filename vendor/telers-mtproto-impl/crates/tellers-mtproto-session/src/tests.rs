//! Session identifier, sequence, and acknowledgement regressions.

use super::*;

struct FixedClock(i64);
impl Clock for FixedClock {
    fn unix_micros(&self) -> i64 {
        self.0
    }
}
struct FixedRandom(u8);
impl RandomSource for FixedRandom {
    fn fill(&mut self, out: &mut [u8]) -> Result<(), Error> {
        out.fill(self.0);
        Ok(())
    }
}
#[test]
fn ids_and_sequences_are_monotonic() {
    let mut state = Snapshot::new(2, &mut FixedRandom(7)).unwrap();
    let clock = FixedClock(1_700_000_000_000_000);
    let first = state.next_message_id(&clock).unwrap();
    let second = state.next_message_id(&clock).unwrap();
    assert_eq!(second, first + 4);
    assert_eq!(state.next_sequence(false).unwrap(), 0);
    assert_eq!(state.next_sequence(true).unwrap(), 1);
    assert_eq!(state.next_sequence(true).unwrap(), 3);
}
#[test]
fn acknowledgements_are_deduplicated() {
    let mut state = Snapshot::new(2, &mut FixedRandom(1)).unwrap();
    state.acknowledge(5).unwrap();
    state.acknowledge(5).unwrap();
    state.acknowledge(9).unwrap();
    assert_eq!(state.take_acknowledgements(1), vec![5]);
    assert_eq!(state.take_acknowledgements(10), vec![9]);
}
