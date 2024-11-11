import { useStopwatchContext } from '@/features/current-session/Stopwatch/StopwatchContext';
import { Button } from '@/ui/button';
import { Input } from '@/ui/input';
import { useEffect, useState } from 'react';

export const StopwatchSessionName: React.FC = () => {
  const { projectName, setProjectName } = useStopwatchContext();
  const [isEditing, setIsEditing] = useState(false);
  const [inputValue, setInputValue] = useState(projectName);

  /**
   * Sync internal state
   * with broadcasted updates
   */
  useEffect(() => {
    setInputValue(projectName);
  }, [projectName]);

  const handleSaveClick = () => {
    setProjectName(inputValue);
    setIsEditing(false);
  };

  return (
    <div className='flex items-center space-x-2'>
      <Input
        value={inputValue}
        onChange={(e) => setInputValue(e.target.value)}
        disabled={!isEditing}
        className='w-64'
      />
      <Button
        onClick={() => (isEditing ? handleSaveClick() : setIsEditing(true))}
      >
        {isEditing ? 'Save Project Name' : 'Change Project Name'}
      </Button>
    </div>
  );
};
